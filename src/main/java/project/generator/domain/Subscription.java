package project.generator.domain;

import com.google.protobuf.ByteString;
import project.generator.config.Operator;
import project.generator.domain.generated.SubscriptionProtos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HomePC on 5/17/2017.
 */
public class Subscription {

    private Map<String, MyPair> fields;

    public Subscription() {
        fields = new HashMap<>();
    }

    public Subscription(Map<String, MyPair> fields) {
        this.fields = fields;
    }

    public Map<String, MyPair> getFields() {
        return this.fields;
    }

    public SubscriptionProtos.Subscription convertToProto() {
        SubscriptionProtos.Subscription.Builder builder = SubscriptionProtos.Subscription.newBuilder();
        for(Map.Entry<String, MyPair> temp : fields.entrySet()) {
            SubscriptionProtos.SubscriptionDomain.Builder subDomainBuilder = SubscriptionProtos.SubscriptionDomain.newBuilder();
            subDomainBuilder.setName(temp.getKey());
            subDomainBuilder.setOperator(temp.getValue().getOperator().toString());
            subDomainBuilder.setValue(ByteString.copyFrom(toByteArray(temp.getValue().getOperand())));
            builder.addTuples(subDomainBuilder.build());
        }
        return builder.build();
    }

    public static Subscription convertToProto(SubscriptionProtos.Subscription subscription) {
        Map<String, MyPair> subFields = new HashMap<>();
        for(SubscriptionProtos.SubscriptionDomain temp : subscription.getTuplesList()) {
            MyPair myPair = new MyPair(Operator.parseString(temp.getOperator()), fromByteArray(temp.getValue().toByteArray()));
            subFields.put(temp.getName(), myPair);
        }
        return new Subscription(subFields);
    }

    private byte[] toByteArray(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    private static Object fromByteArray(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }
}
