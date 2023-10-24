package io.legacyfighter.cabs.geolocation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.legacyfighter.cabs.geolocation.address.Address;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ApplicationScope
public class GeocodingService {

    private static final Logger LOG = LoggerFactory.getLogger(GeocodingService.class);

    private final RestTemplate rest;
    private final Cache cache;
    private final RateLimit rateLimit = new RateLimit(1, Duration.ofSeconds(1));

    public GeocodingService(RestTemplate rest) {
        this.rest = rest;
        cache = new ConcurrentMapCache("geocoding");
    }

    public double[] geocodeAddress(Address address) {
        return cache.get(asParams(address),
            () -> rateLimit.execute(
                () -> geocodeAddressRemotely(address)
            )
        );
    }

    private List<NameValuePair> asParams(Address address) {
        Map<String, String> params = new HashMap<>();
        params.put("country", address.getCountry());
        params.put("city", address.getCity());
        params.put("street", streetOf(address));
        params.put("format", "jsonv2");
        params.put("limit", "1");
        if (address.getPostalCode() != null) {
            params.put("postalcode", address.getPostalCode());
        }
        return params.entrySet().stream()
            .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    }

    private double[] geocodeAddressRemotely(Address address) {
        double[] geocoded = new double[2];

        try {
            URI uri = new URIBuilder("https://nominatim.openstreetmap.org/search")
                .addParameters(asParams(address)).build();

            ResponseEntity<GeoLocation[]> resp = rest.getForEntity(uri, GeoLocation[].class);

            GeoLocation[] locs = resp.getBody();
            if (locs == null || locs.length != 1) {
                throw new IOException("No geolocation found for address: " + address);
            }
            GeoLocation loc = locs[0];

            geocoded[0] = loc.lat; //latitude
            geocoded[1] = loc.lon; //longitude

            LOG.info("Geocoded address: {} to {}", address, geocoded);
            return geocoded;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private String streetOf(Address address) {
        StringBuilder sb = new StringBuilder(address.getStreet());
        if (address.getBuildingNumber() != null) {
            sb.append(" ").append(address.getBuildingNumber());
        }
        if (address.getAdditionalNumber() != null) {
            sb.append("/").append(address.getAdditionalNumber());
        }
        return sb.toString();
    }

    @JsonSerialize
    private static class GeoLocation {
        final double lat;
        final double lon;

        @JsonCreator
        public GeoLocation(
            @JsonProperty String lat,
            @JsonProperty String lon
        ) {
            this.lat = Double.parseDouble(lat);
            this.lon = Double.parseDouble(lon);
        }
    }
}
