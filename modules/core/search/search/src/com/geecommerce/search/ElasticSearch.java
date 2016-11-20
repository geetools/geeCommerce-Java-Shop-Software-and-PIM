package com.geecommerce.search;

import java.util.Set;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.geecommerce.core.Char;
import com.geecommerce.core.Str;
import com.geecommerce.core.config.MerchantConfig;

public enum ElasticSearch // TODO com.geecommerce.catalog.search.ElasticSearch
{
    CLIENT;

    Client client = null;

    ElasticSearch() {
        Settings settings = ImmutableSettings.settingsBuilder().put("node.client", true)
            .classLoader(Settings.class.getClassLoader()).build();

        String nodesCSV = MerchantConfig.GET.val(MerchantConfig.ELASTICSEARCH_NODES);

        if (!Str.isEmpty(nodesCSV)) {
            client = new TransportClient(settings);

            Set<String> nodeAddresses = Strings.commaDelimitedListToSet(nodesCSV);

            for (String node : nodeAddresses) {
                if (node.indexOf(Char.COLON) != -1) {
                    String[] sp = node.split(Str.COLON);
                    String host = sp[0];
                    int port = Integer.parseInt(sp[1]);

                    System.out.println("Connection to: " + host + ":" + port);

                    ((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(host, port));
                } else {
                    System.out.println("Connection to: " + node + ":9300");

                    ((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(node, 9300));
                }
            }
        } else {
            System.out.println("Connection to: 127.0.0.1:9300");

            client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("127.0.0.1", 9300));
        }
    }

    public Client get() {
        return client;
    }
}
