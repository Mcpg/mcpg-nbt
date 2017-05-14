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

import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import pl.mcpg.nbt.Tag;
import pl.mcpg.nbt.TagByte;
import pl.mcpg.nbt.TagByteArray;
import pl.mcpg.nbt.TagCompound;
import pl.mcpg.nbt.TagCompound.CompoundContent;
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

public class TagInputStream extends FilterInputStream
{
    private DataInputStream stream;

    public TagInputStream(InputStream in, boolean compressed) throws IOException
    {
        super(compressed ? new GZIPInputStream(in) : in);
        this.stream = new DataInputStream(this.in);
    }

    public Tag<?> readTag() throws IOException
    {
        int id = stream.readUnsignedByte();
        byte[] rawName = {};
        if (id != TagType.TAG_END.getId())
        {
            rawName = new byte[stream.readShort()];
            stream.readFully(rawName);
        }
        return readPayload(new String(rawName, StandardCharsets.UTF_8), id);
    }

    public Tag<?> readPayload(String name, int id) throws IOException
    {
        TagType type = TagType.byId(id);
        if (type == null)
        {
            throw new IllegalStateException("Unknown tag with id " + id + "!");
        }
        switch (type)
        {
            case TAG_END:
                return new TagEnd();
            case TAG_BYTE:
                return new TagByte(name, stream.readByte());
            case TAG_SHORT:
                return new TagShort(name, stream.readShort());
            case TAG_INT:
                return new TagInt(name, stream.readInt());
            case TAG_LONG:
                return new TagLong(name, stream.readLong());
            case TAG_DOUBLE:
                return new TagDouble(name, stream.readDouble());
            case TAG_FLOAT:
                return new TagFloat(name, stream.readFloat());
            case TAG_STRING:
                byte[] rawString = new byte[stream.readShort()];
                stream.readFully(rawString);
                return new TagString(name, new String(rawString, StandardCharsets.UTF_8));
            case TAG_COMPOUND:
                Tag<?> nextTag;
                CompoundContent compoundContent = new CompoundContent();
                while ((nextTag = readTag()).getType() != TagType.TAG_END)
                {
                    compoundContent.addTag(nextTag);
                }
                return new TagCompound(name, compoundContent);
            case TAG_LIST:
                int tagId = stream.readUnsignedByte();
                TagType listType = TagType.byId(tagId);
                if (listType == null)
                {
                    throw new IllegalStateException("Trying to read TAG_List with tags of id " + tagId + "!");
                }
                int length = stream.readInt();
                List<Tag<?>> tagList = new ArrayList<>();
                for (int i = 0; i < length; i++)
                {
                    tagList.add(readPayload("", listType.getId()));
                }
                return new TagList(name, tagList, listType);
            case TAG_BYTE_ARRAY:
                byte[] byteArrayPayload = new byte[stream.readInt()];
                stream.readFully(byteArrayPayload);
                return new TagByteArray(name, byteArrayPayload);
            case TAG_INT_ARRAY:
                int[] intArrayPayload = new int[stream.readInt()];
                for (int i = 0; i < intArrayPayload.length; i++)
                {
                    intArrayPayload[i] = stream.readInt();
                }
                return new TagIntArray(name, intArrayPayload);
            default:
                return null;
        }
    }
}
