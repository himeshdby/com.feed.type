import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


public class FeedImpl {
    private static ObjectReader objectReader;
    public static void getData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        List<Transaction> transaction = getTransactions(mapper);
        List<Client> client = getClients(mapper);
        List<FeedType> feedType = getFeedTypes(mapper);
        Map<String,String> map = new HashMap<>();
        Map<String, String> clientName = client.stream().filter(i -> transaction.stream().anyMatch(j -> j.getClientId().equalsIgnoreCase(i.getId()))).collect(Collectors.toMap(Client::getId, Client::getClientName));
        Map<String, String> feedTypeName = feedType.stream().filter(i -> transaction.stream().anyMatch(j -> j.getFeedType().equalsIgnoreCase(i.getId()))).collect(Collectors.toMap(FeedType::getId, FeedType::getFeedTypeName));
        Map<String, Integer> resultsQuantity = transaction.stream().collect(
                Collectors.toMap(
                        Transaction::getClientId,
                        Transaction::getFeedQantity,
                        Integer::sum
                )
        );
        Map.Entry<String, Integer> topFeedValue = resultsQuantity.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();

        long averageCountValue = transaction.stream().filter(i -> i.getClientId().equalsIgnoreCase(topFeedValue.getKey())).map(Transaction::getDate).count();
        map.put("client_name",clientName.get(topFeedValue.getKey()));
                map.put("total_feed_quantity",String.valueOf(topFeedValue.getValue()));
                map.put("top_feed_type",String.valueOf(topFeedValue.getValue())+"kg @super");
        long averageValue = topFeedValue.getValue().longValue()/averageCountValue;
        map.put("average_monthly_feed_quantity",String.valueOf(averageValue)+"kg");
        map.entrySet().forEach(e->System.out.println(e));
    }

    private static List<FeedType> getFeedTypes(ObjectMapper mapper) throws IOException {
        InputStream is2 = FeedImpl.class.getResourceAsStream("/FeedType.json");
        objectReader = mapper.reader().forType(new TypeReference<List<FeedType>>() {
        });

        List<FeedType> feedType = objectReader.readValue(is2);
        return feedType;
    }

    private static List<Client> getClients(ObjectMapper mapper) throws IOException {
        InputStream is1 = FeedImpl.class.getResourceAsStream("/Client.json");
        objectReader = mapper.reader().forType(new TypeReference<List<Client>>() {
        });
        List<Client> client = objectReader.readValue(is1);
        return client;
    }

    private static List<Transaction> getTransactions(ObjectMapper mapper) throws IOException {
        InputStream is = FeedImpl.class.getResourceAsStream("/FeedTransaction.json");
        objectReader = mapper.reader().forType(new TypeReference<List<Transaction>>() {
        });
        List<Transaction> transaction = objectReader.readValue(is);
        return transaction;
    }


    public static void main(String[] args) throws IOException {
        FeedImpl.getData();
    }
}


