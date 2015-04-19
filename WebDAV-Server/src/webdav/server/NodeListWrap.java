package webdav.server;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListWrap implements Iterator<Node>
{
    private NodeListWrap(NodeList list)
    {
        this.list = list;
        this.maxIndex = list.getLength();
        this.index = 0;
    }
    private final NodeList list;
    private final int maxIndex;
    private int index;
    
    
    public static Stream<Node> getStream(NodeList list)
    {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(new NodeListWrap(list), Spliterator.NONNULL),
                false);
    }
    
    public static byte[] getBytes(Document doc)
    {
        try
        {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(boas);

            transformer.transform(source, result);

            return boas.toByteArray();
        }
        catch (TransformerException ex)
        {
            return new byte[0];
        }
    }
    
    

    @Override
    public boolean hasNext()
    {
        return index < maxIndex;
    }

    @Override
    public Node next()
    {
        return list.item(index++);
    }
}
