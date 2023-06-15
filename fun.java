public class CustomOrder implements Serializable, EDIWritable{
private int number;
private IntegerDecoder numberDecoder;
private String sender;
private String message;
private int price;
private IntegerDecoder priceDecoder;
public CustomOrder() {
    numberDecoder = new IntegerDecoder();
    priceDecoder = new IntegerDecoder();
}
public int getNumber() {
    return number;
}
public void setNumber(int number) {
    this.number = number;
}
public void setSender(String sender) {
    this.sender = sender;
}
public String getSender() {
    return sender;
}
public String getMessage() {
    return message;
}
public void setMessage(String message) {
    this.message = message;
}
public int getPrice() {
    return price;
}
public void setPrice(int price) {
    this.price = price;
}
public  void write(Writer writer, Delimiters delimiters) throws IOException {
    // TODO Auto-generated method stub
     Writer nodeWriter = writer;

        if(number != 0) {
            nodeWriter.write(delimiters.escape(numberDecoder.encode(number)));
        }
        nodeWriter.write(delimiters.getField());

        if(sender != null) {
            nodeWriter.write(delimiters.escape(sender.toString()));
        }
        nodeWriter.write(delimiters.getField());

        if(message != null) {
            nodeWriter.write(delimiters.escape(message.toString()));
        }
        nodeWriter.write(delimiters.getField());
        if(price != 0) {
            nodeWriter.write(delimiters.escape(priceDecoder.encode(price)));
        }
        //nodeWriter.write(delimiters.getField());

        writer.write(delimiters.getSegmentDelimiter());
        writer.flush();

}
}

public class CustomOrderFactory {
 private Smooks smooks;
 private Delimiters delimiters;
 public static CustomOrderFactory getInstance() throws IOException, SAXException {
        return new CustomOrderFactory();
    }
    public void addConfigurations(InputStream resourceConfigStream) throws SAXException, IOException {
        smooks.addConfigurations(resourceConfigStream);
    }
    public void toEDI(CustomOrder instance, Writer writer) throws IOException {
        instance.write(writer, delimiters);
    }

    private CustomOrderFactory() throws IOException, SAXException {
        smooks = new Smooks(CustomOrderFactory.class.getResourceAsStream("smooks-config.xml"));
        System.out.println("smooks is prepared");
        try {   
            Edimap edimap = EDIConfigDigester.digestConfig(CustomOrderFactory.class.getResourceAsStream("custom-order-mapping.xml"));
            System.out.println("ediMap is prepared");
            delimiters = edimap.getDelimiters();
            System.out.println("delimeter is prepared");
        } catch(EDIConfigurationException e) {
            IOException ioException = new IOException("Exception reading EDI Mapping model.");
            ioException.initCause(e);
            throw ioException;
        }
    }
}


Main smooksMain = new Main();
ExecutionContext executionContext = smooksMain.smooks.createExecutionContext();
org.milyn.payload.JavaResult result = smooksMain.runSmooksTransform(executionContext);
CustomOrder custOrder = (CustomOrder) result.getBean("customer");
// Prepare edi data from java object custOrder
CustomOrderFactory customOrderFactory = CustomOrderFactory.getInstance();
OutputStream os = new FileOutputStream("createdEDIFile.edi");
customOrderFactory.toEDI(custOrder, new OutputStreamWriter(os));    
System.out.println("Edi file is created from java object");