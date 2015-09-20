package http;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class ExtendableByteBuffer
{
    public ExtendableByteBuffer()
    { }
    
    protected int internalBufferSize = 1500;
    protected List<byte[]> content = new ArrayList<>();
    protected int totalLength = 0;
    
    
    
    public ExtendableByteBuffer setInternalBufferSize(int internalBufferSize)
    {
        this.internalBufferSize = internalBufferSize;
        return this;
    }
    
    
    
    
    
    
    
    
    
    
    public ExtendableByteBuffer write(ExtendableByteBuffer byteBuffer)
    {
        content.addAll(byteBuffer.content);
        totalLength += byteBuffer.totalLength;
        
        return this;
    }
    public ExtendableByteBuffer write(InputStream is, int stopSize) throws IOException
    {
        byte[] data = new byte[internalBufferSize];
        int len;
        while((len = is.read(data)) >= 0)
            if(len > 0)
            {
                byte[] d = new byte[len];
                System.arraycopy(data, 0, d, 0, len);
                content.add(d);
                totalLength += len;
                
                if(totalLength >= stopSize)
                    return this;
            }
        
        return this;
    }
    public ExtendableByteBuffer write(InputStream is) throws IOException
    {
        byte[] data = new byte[internalBufferSize];
        int len;
        while((len = is.read(data)) >= 0)
            if(len > 0)
            {
                byte[] d = new byte[len];
                System.arraycopy(data, 0, d, 0, len);
                content.add(d);
                totalLength += len;
            }
        
        return this;
    }
    public ExtendableByteBuffer writeOnce(InputStream is) throws IOException
    {
        byte[] data = new byte[internalBufferSize];
        int len;
        if((len = is.read(data)) >= 0)
        {
            byte[] d = new byte[len];
            System.arraycopy(data, 0, d, 0, len);
            content.add(d);
            totalLength += len;
        }
        
        return this;
    }
    public ExtendableByteBuffer write(Byte value)
    {
        write((byte)value);
        return this;
    }
    public ExtendableByteBuffer write(byte value)
    {
        content.add(new byte[] { value });
        totalLength++;
        
        return this;
    }
    public ExtendableByteBuffer write(Character value)
    {
        write((char)value);
        return this;
    }
    public ExtendableByteBuffer write(char value)
    {
        content.add(new byte[] { (byte)value });
        totalLength++;
        
        return this;
    }
    public ExtendableByteBuffer write(byte[] values, int len)
    {
        if(len == 0)
            return this;
        
        byte[] newBytes = new byte[len];
        System.arraycopy(values, 0, newBytes, 0, len);
        content.add(newBytes);
        totalLength += newBytes.length;
        
        return this;
    }
    public ExtendableByteBuffer write(byte[] values, int len, int index)
    {
        if(len == 0 || index + len >= values.length)
            return this;
        
        byte[] newBytes = new byte[len];
        System.arraycopy(values, index, newBytes, 0, len);
        content.add(newBytes);
        totalLength += newBytes.length;
        
        return this;
    }
    public ExtendableByteBuffer write(byte[] values)
    {
        content.add(values);
        totalLength += values.length;
        
        return this;
    }
    public ExtendableByteBuffer write(char[] values, int len)
    {
        if(len == 0)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(char[] values, int len, int index)
    {
        if(len == 0 || index + len >= values.length)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i + index];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(char[] values)
    {
        byte[] data = new byte[values.length];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Byte[] values, int len)
    {
        if(len == 0)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Byte[] values, int len, int index)
    {
        if(len == 0 || index + len >= values.length)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i + index];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Byte[] values)
    {
        byte[] data = new byte[values.length];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Character[] values, int len)
    {
        if(len == 0)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(char)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Character[] values, int len, int index)
    {
        if(len == 0 || index + len >= values.length)
            return this;
        
        byte[] data = new byte[len];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(char)values[i + index];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Character[] values)
    {
        byte[] data = new byte[values.length];
        for(int i = 0; i < data.length; i++)
            data[i] = (byte)(char)values[i];
        
        write(data);
        
        return this;
    }
    public ExtendableByteBuffer write(Iterable<Byte> values)
    {
        ArrayList<Byte> bytes = new ArrayList<>();
        values.forEach(c -> bytes.add(c));
        
        write(bytes.stream().toArray(Byte[]::new));
        
        return this;
    }
    
    
    
    
    
    
    
    
    public int length()
    {
        return totalLength;
    }
    
    
    
    
    
    
    
    
    
    
    
    public ByteBuffer toByteBuffer()
    {
        byte[] contentData = new byte[totalLength];
        int index = 0;
        for(byte[] bs : content)
        {
            System.arraycopy(bs, 0, contentData, index, bs.length);
            index += bs.length;
        }
        
        return ByteBuffer.wrap(contentData);
    }
    public byte[] toBytes()
    {
        return toByteBuffer().array();
    }
    public char[] toChars()
    {
        return toByteBuffer().asCharBuffer().array();
    }
    public double[] toDoubles()
    {
        return toByteBuffer().asDoubleBuffer().array();
    }
    public float[] toFloats()
    {
        return toByteBuffer().asFloatBuffer().array();
    }
    public int[] toInts()
    {
        return toByteBuffer().asIntBuffer().array();
    }
    public long[] toLongs()
    {
        return toByteBuffer().asLongBuffer().array();
    }
    public short[] toShorts()
    {
        return toByteBuffer().asShortBuffer().array();
    }
    @Override
    public String toString()
    {
        return new String(toBytes());
    }
    public String toString(String charset) throws UnsupportedEncodingException
    {
        return new String(toBytes(), charset);
    }
    public String toString(Charset charset)
    {
        return new String(toBytes(), charset);
    }
    public Collection<String> toLines()
    {
        return Arrays.asList(toString()
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .split("\n"));
    }
    public Collection<String> toLines(String charset) throws UnsupportedEncodingException
    {
        return Arrays.asList(toString(charset)
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .split("\n"));
    }
    public Collection<String> toLines(Charset charset)
    {
        return Arrays.asList(toString(charset)
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .split("\n"));
    }
    
    public Document toXML() throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        DocumentBuilder xmlBuilder = factory.newDocumentBuilder();

        ByteArrayInputStream bais = new ByteArrayInputStream(toBytes());
        return xmlBuilder.parse(bais);
    }
}
