package com.bootcamptoprod.customresttemplateexchangetagsprovider.config;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags;
import org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTagsProvider;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The type Bootcamp to prod custom rest template exchange tags provider.
 * Useful for adding our own tags in rest template metrics. This will override the default tags that are provided by the framework.
 */
@Component
public class BootcampToProdCustomRestTemplateExchangeTagsProvider implements RestTemplateExchangeTagsProvider {
    @Override
    public Iterable<Tag> getTags(String urlTemplate, HttpRequest request, ClientHttpResponse response) {
        Tag uriTag = StringUtils.hasText(urlTemplate) ? RestTemplateExchangeTags.uri(urlTemplate) : RestTemplateExchangeTags.uri(request);

        // Here we are adding the tags related to HTTP method, HTTP status, client name and outcome
        var tags = Tags.of(RestTemplateExchangeTags.method(request), uriTag, RestTemplateExchangeTags.status(response), RestTemplateExchangeTags.clientName(request), RestTemplateExchangeTags.outcome(response));

        // Extracting query parameters from URI
        String uri = request.getURI().toString();
        MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(uri).build().getQueryParams();

        // Optional tag which will be present in metrics only when the condition is evaluated to true
        if (parameters.containsKey("id")) {
            tags = tags.and(Tag.of("userId", parameters.get("id").get(0)));
        }

        // Custom tag which will be present in all the controller metrics
        tags = tags.and(Tag.of("tag", "value"));

        return tags;
    }

}
