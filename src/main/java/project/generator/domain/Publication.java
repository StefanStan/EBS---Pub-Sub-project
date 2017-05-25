package project.generator.domain;

import com.google.protobuf.ByteString;
import project.generator.domain.generated.PublicationProtos;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HomePC on 5/17/2017.
 */
public class Publication implements Serializable {
    private Map<String, Object> fields;

    public Publication(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }

    public PublicationProtos.Publication convert() {
        PublicationProtos.Publication.Builder builder = PublicationProtos.Publication.newBuilder();
        for(Map.Entry<String, Object> temp : fields.entrySet()) {
            PublicationProtos.PublicationDomain.Builder pubDomainBuilder = PublicationProtos.PublicationDomain.newBuilder();
            pubDomainBuilder.setName(temp.getKey());
            pubDomainBuilder.setValue(ByteString.copyFrom(toByteArray(temp.getValue())));
            builder.addTuples(pubDomainBuilder.build());
        }
        return builder.build();
    }

    public static Publication convert(PublicationProtos.Publication publication) {
        Map<String, Object> pubFields = new HashMap<>();
        for(PublicationProtos.PublicationDomain temp : publication.getTuplesList()) {
            pubFields.put(temp.getName(), fromByteArray(temp.getValue().toByteArray()));
        }
        return new Publication(pubFields);
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
