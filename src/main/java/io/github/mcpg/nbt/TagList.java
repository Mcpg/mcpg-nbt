/*
 * Copyright (c) 2018 Paweł Cholewa
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.mcpg.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TagList extends Tag<List<Tag<?>>>
{
    private TagType listType;

    public TagList(String name, List<Tag<?>> value, TagType listType)
    {
        super(name, value, TagType.TAG_LIST);
        this.listType = listType;
    }

    public TagList(String name)
    {
        super(name);
        this.type = TagType.TAG_LIST;
    }

    @Override
    public void writeTagPayload(DataOutputStream outputStream) throws IOException
    {
        outputStream.writeByte(listType.getId());
        outputStream.writeInt(value.size());
        for (Tag<?> tag : value)
        {
            tag.writeTagPayload(outputStream);
        }
    }

    @Override
    public void readTagPayload(DataInputStream inputStream) throws IOException
    {
        listType = TagType.byId(inputStream.readUnsignedByte());
        int size = inputStream.readInt();
        if (size > 0 && (listType == TagType.TAG_END || listType == null))
        {
            throw new IllegalStateException("Incorrect list type!");
        }
        if (listType == null)
        {
            listType = TagType.TAG_END;
        }
        if (value == null)
        {
            value = new ArrayList<>(size);
        }
        else
        {
            value.clear();
        }
        for (int i = 0; i < size; i++)
        {
            Object obj;
            try
            {
                Constructor<?> constructor = listType.getClazz().getConstructor(String.class);
                obj = constructor.newInstance("");
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                throw new IllegalStateException("Couldn't create object from type " + type + "!", e);
            }
            if (!(obj instanceof Tag<?>))
            {
                throw new IllegalStateException("Readed object isn't Tag<?>!");
            }
            Tag<?> tag = (Tag<?>) obj;
            tag.readTagPayload(inputStream);
            value.add(tag);
        }
    }

    public TagType getListType()
    {
        return listType;
    }
}
