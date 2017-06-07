/*
 * Copyright (c) 2017 Paweł Cholewa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * Copyright (c) 2017 Paweł Cholewa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pl.mcpg.nbt.io;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

import pl.mcpg.nbt.Tag;
import pl.mcpg.nbt.TagByte;
import pl.mcpg.nbt.TagByteArray;
import pl.mcpg.nbt.TagCompound;
import pl.mcpg.nbt.TagDouble;
import pl.mcpg.nbt.TagEnd;
import pl.mcpg.nbt.TagFloat;
import pl.mcpg.nbt.TagInt;
import pl.mcpg.nbt.TagIntArray;
import pl.mcpg.nbt.TagList;
import pl.mcpg.nbt.TagLong;
import pl.mcpg.nbt.TagShort;
import pl.mcpg.nbt.TagString;
import pl.mcpg.nbt.TagType;

public class TagOutputStream extends FilterOutputStream
{
    private DataOutputStream stream;

    public TagOutputStream(OutputStream out, boolean compressed) throws IOException
    {
        super(compressed ? new DataOutputStream(new GZIPOutputStream(out)) : new DataOutputStream(out));
        this.stream = (DataOutputStream) this.out; // use FilterOutputStream's out, not this from constructor
    }

    public void writeTag(Tag<?> tag) throws IOException
    {
        stream.write(tag.getType().getId());
        if (tag.getType() != TagType.TAG_END)
        {
            byte[] nameRaw = tag.getName().getBytes(StandardCharsets.UTF_8);
            stream.writeShort(nameRaw.length);
            stream.write(nameRaw);
        }
        writePayload(tag);
    }

    public void writePayload(Tag<?> tag) throws IOException
    {
        switch (tag.getType())
        {
            case TAG_END:
                break;
            case TAG_BYTE:
                stream.writeByte(((TagByte) tag).getValue());
                break;
            case TAG_SHORT:
                stream.writeShort(((TagShort) tag).getValue());
                break;
            case TAG_INT:
                stream.writeInt(((TagInt) tag).getValue());
                break;
            case TAG_LONG:
                stream.writeLong(((TagLong) tag).getValue());
                break;
            case TAG_FLOAT:
                stream.writeFloat(((TagFloat) tag).getValue());
                break;
            case TAG_DOUBLE:
                stream.writeDouble(((TagDouble) tag).getValue());
                break;
            case TAG_STRING:
                byte[] stringPayload = ((TagString) tag).getValue().getBytes(StandardCharsets.UTF_8);
                stream.writeShort(stringPayload.length);
                stream.write(stringPayload);
                break;
            case TAG_LIST:
                TagList tagList = (TagList) tag;
                stream.writeByte(tagList.getListType().getId());
                stream.writeInt(tagList.getValue().size());
                for (Tag<?> listTag : tagList.getValue())
                {
                    if (listTag != null)
                    {
                        writePayload(listTag);
                    }
                }
                break;
            case TAG_COMPOUND:
                for (Tag<?> compoundTag : ((TagCompound) tag).getValue().getTagsList())
                {
                    writeTag(compoundTag);
                }
                writeTag(new TagEnd());
                break;
            case TAG_BYTE_ARRAY:
                byte[] byteArray = ((TagByteArray) tag).getValue();
                stream.writeInt(byteArray.length);
                for (byte arrayByte : byteArray)
                {
                    stream.writeByte(arrayByte);
                }
                break;
            case TAG_INT_ARRAY:
                int[] intArray = ((TagIntArray) tag).getValue();
                stream.writeInt(intArray.length);
                for (int arrayInt : intArray)
                {
                    stream.writeInt(arrayInt);
                }
                break;
        }
    }
}
