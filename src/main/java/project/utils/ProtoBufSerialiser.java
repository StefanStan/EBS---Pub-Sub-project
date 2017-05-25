package project.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessageV3;

import java.lang.reflect.InvocationTargetException;

import static com.esotericsoftware.kryo.Kryo.NULL;

/**
 * Created by Nyan on 5/25/2017.
 */
public abstract class ProtoBufSerialiser<T extends GeneratedMessageV3> extends Serializer<T> {

    private java.lang.reflect.Method parseFromMethod = null;

    public void write(Kryo kryo, Output output, T protobufMessage) {
        // If our protobuf is null
        if (protobufMessage == null) {
            // Write our special null value
            output.writeByte(NULL);
            output.flush();

            // and we're done
            return;
        }

        // Otherwise serialize protobuf to a byteArray
        byte[] bytes = protobufMessage.toByteArray();

        // Write the length of our byte array
        output.writeInt(bytes.length + 1, true);

        // Write the byte array out
        output.writeBytes(bytes);
        output.flush();
    }

    public T read(Kryo kryo, Input input, Class type) {
        // Read the length of our byte array
        int length = input.readInt(true);

        // If the length is equal to our special null value
        if (length == NULL) {
            // Just return null
            return null;
        }
        // Otherwise read the byte array length
        byte[] bytes = input.readBytes(length - 1);
        try {
            // Deserialize protobuf
            return (T) (getParseFromMethod(type).invoke(type, bytes));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to deserialize protobuf "+e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Unable to deserialize protobuf "+e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to deserialize protobuf "+e.getMessage(), e);
        }
    }

    /**
     * Caches method reflection lookup
     * @throws NoSuchMethodException
     */
    private java.lang.reflect.Method getParseFromMethod(Class<T> type) throws NoSuchMethodException {
        if (parseFromMethod == null) {
            parseFromMethod = type.getMethod("parseFrom", byte[].class);
        }
        return parseFromMethod;
    }

    @Override
    public boolean getAcceptsNull () {
        return true;
    }
}
