package com.iCo6.util.nbt;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public final class NBTOutputStream implements Closeable {
  private final DataOutputStream os;
  
  public NBTOutputStream(OutputStream os) throws IOException {
    this(os, true);
  }
  
  public NBTOutputStream(OutputStream os, boolean compressed) throws IOException {
    this.os = new DataOutputStream(compressed ? new GZIPOutputStream(os) : os);
  }
  
  public void writeTag(Tag tag) throws IOException {
    int type = NBTUtils.getTypeCode((Class)tag.getClass());
    String name = tag.getName();
    byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);
    this.os.writeByte(type);
    this.os.writeShort(nameBytes.length);
    this.os.write(nameBytes);
    if (type == 0)
      throw new IOException("Named TAG_End not permitted."); 
    writeTagPayload(tag);
  }
  
  private void writeTagPayload(Tag tag) throws IOException {
    int type = NBTUtils.getTypeCode((Class)tag.getClass());
    switch (type) {
      case 0:
        writeEndTagPayload((EndTag)tag);
        return;
      case 1:
        writeByteTagPayload((ByteTag)tag);
        return;
      case 2:
        writeShortTagPayload((ShortTag)tag);
        return;
      case 3:
        writeIntTagPayload((IntTag)tag);
        return;
      case 4:
        writeLongTagPayload((LongTag)tag);
        return;
      case 5:
        writeFloatTagPayload((FloatTag)tag);
        return;
      case 6:
        writeDoubleTagPayload((DoubleTag)tag);
        return;
      case 7:
        writeByteArrayTagPayload((ByteArrayTag)tag);
        return;
      case 8:
        writeStringTagPayload((StringTag)tag);
        return;
      case 9:
        writeListTagPayload((ListTag)tag);
        return;
      case 10:
        writeCompoundTagPayload((CompoundTag)tag);
        return;
    } 
    throw new IOException("Invalid tag type: " + type + ".");
  }
  
  private void writeByteTagPayload(ByteTag tag) throws IOException {
    this.os.writeByte(tag.getValue().byteValue());
  }
  
  private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
    byte[] bytes = tag.getValue();
    this.os.writeInt(bytes.length);
    this.os.write(bytes);
  }
  
  private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
    for (Tag childTag : tag.getValue().values())
      writeTag(childTag); 
    this.os.writeByte(0);
  }
  
  private void writeListTagPayload(ListTag<?> tag) throws IOException {
    Class<? extends Tag> clazz = (Class)tag.getType();
    List<Tag> tags = (List)tag.getValue();
    int size = tags.size();
    this.os.writeByte(NBTUtils.getTypeCode(clazz));
    this.os.writeInt(size);
    for (int i = 0; i < size; i++)
      writeTagPayload(tags.get(i)); 
  }
  
  private void writeStringTagPayload(StringTag tag) throws IOException {
    byte[] bytes = tag.getValue().getBytes(NBTConstants.CHARSET);
    this.os.writeShort(bytes.length);
    this.os.write(bytes);
  }
  
  private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
    this.os.writeDouble(tag.getValue().doubleValue());
  }
  
  private void writeFloatTagPayload(FloatTag tag) throws IOException {
    this.os.writeFloat(tag.getValue().floatValue());
  }
  
  private void writeLongTagPayload(LongTag tag) throws IOException {
    this.os.writeLong(tag.getValue().longValue());
  }
  
  private void writeIntTagPayload(IntTag tag) throws IOException {
    this.os.writeInt(tag.getValue().intValue());
  }
  
  private void writeShortTagPayload(ShortTag tag) throws IOException {
    this.os.writeShort(tag.getValue().shortValue());
  }
  
  private void writeEndTagPayload(EndTag tag) {}
  
  public void close() throws IOException {
    this.os.close();
  }
}


