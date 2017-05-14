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

package pl.mcpg.nbt.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import pl.mcpg.nbt.Tag;
import pl.mcpg.nbt.TagByte;
import pl.mcpg.nbt.TagByteArray;
import pl.mcpg.nbt.TagCompound;
import pl.mcpg.nbt.TagCompound.CompoundContent;
import pl.mcpg.nbt.TagDouble;
import pl.mcpg.nbt.TagFloat;
import pl.mcpg.nbt.TagInt;
import pl.mcpg.nbt.TagIntArray;
import pl.mcpg.nbt.TagList;
import pl.mcpg.nbt.TagLong;
import pl.mcpg.nbt.TagShort;
import pl.mcpg.nbt.TagString;
import pl.mcpg.nbt.TagType;
import pl.mcpg.nbt.io.TagInputStream;
import pl.mcpg.nbt.io.TagOutputStream;

public class NBTTest
{
    @Test
    public void nbtTest() throws IOException
    {
        TagCompound root = new TagCompound("Abc", new CompoundContent());
        TagByte tagByte = new TagByte("byte", (byte) 0xab);
        TagShort tagShort = new TagShort("short", (short) 5464);
        TagInt tagInt = new TagInt("int", 3455334);
        TagFloat tagFloat = new TagFloat("float", 31.343f);
        TagDouble tagDouble = new TagDouble("double", 533.3533433);
        TagByteArray tagByteArray = new TagByteArray("byteArray", new byte[256]);
        for (int i = 0; i < tagByteArray.getValue().length; i++)
        {
            tagByteArray.getValue()[i] = (byte) i;
        }
        TagIntArray tagIntArray = new TagIntArray("intArray", new int[20]);
        for (int i = 0; i < tagIntArray.getValue().length; i++)
        {
            tagIntArray.getValue()[i] = (int) (i * 3.5);
        }
        TagString tagString = new TagString("string", "abcdefghijklmnopqrstuwxyz, Witaj, świecie! Hello, world!");
        TagList tagList = new TagList("list", new ArrayList<>(100), TagType.TAG_BYTE);
        for (int i = 0; i < 100; i++)
        {
            tagList.getValue().add(new TagByte("", (byte) i));
        }
        TagLong tagLong = new TagLong("long", Long.MAX_VALUE - 10);
        root.getValue().addTag(tagByte);
        root.getValue().addTag(tagShort);
        root.getValue().addTag(tagInt);
        root.getValue().addTag(tagFloat);
        root.getValue().addTag(tagDouble);
        root.getValue().addTag(tagByteArray);
        root.getValue().addTag(tagIntArray);
        root.getValue().addTag(tagString);
        root.getValue().addTag(tagList);
        root.getValue().addTag(tagLong);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TagOutputStream outputStream = new TagOutputStream(byteArrayOutputStream, false);
        outputStream.writeTag(root);
        byte[] output = byteArrayOutputStream.toByteArray();
        outputStream.close();
        //
        TagInputStream inputStream = new TagInputStream(new ByteArrayInputStream(output), false);
        Tag<?> tag = inputStream.readTag();
        if (!(tag instanceof TagCompound))
        {
            Assert.fail("Loaded tag isn't TAG_Compound");
        }
        TagCompound compound = (TagCompound) tag;
        List<Tag<?>> tags = compound.getValue().getTagsList();
        Tag<?> actual = tags.get(0);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "byte");
        Assert.assertEquals(actual.getType(), TagType.TAG_BYTE);
        Assert.assertEquals(actual.getValue(), tagByte.getValue());
        actual = tags.get(1);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "short");
        Assert.assertEquals(actual.getType(), TagType.TAG_SHORT);
        Assert.assertEquals(actual.getValue(), tagShort.getValue());
        actual = tags.get(2);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "int");
        Assert.assertEquals(actual.getType(), TagType.TAG_INT);
        Assert.assertEquals(actual.getValue(), tagInt.getValue());
        actual = tags.get(3);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "float");
        Assert.assertEquals(actual.getType(), TagType.TAG_FLOAT);
        Assert.assertEquals(actual.getValue(), tagFloat.getValue());
        actual = tags.get(4);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "double");
        Assert.assertEquals(actual.getType(), TagType.TAG_DOUBLE);
        Assert.assertEquals(actual.getValue(), tagDouble.getValue());
        actual = tags.get(5);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "byteArray");
        Assert.assertEquals(actual.getType(), TagType.TAG_BYTE_ARRAY);
        byte[] loadedBytes = (byte[]) actual.getValue();
        Assert.assertEquals(loadedBytes.length, 256);
        for (int i = 0; i < loadedBytes.length; i++)
        {
            Assert.assertEquals(loadedBytes[i] & 0xff, i & 0xff);
        }
        actual = tags.get(6);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "intArray");
        Assert.assertEquals(actual.getType(), TagType.TAG_INT_ARRAY);
        int[] loadedInts = (int[]) actual.getValue();
        Assert.assertEquals(loadedInts.length, 20);
        for (int i = 0; i < tagIntArray.getValue().length; i++)
        {
            Assert.assertEquals(loadedInts[i], (int) (i * 3.5));
        }
        actual = tags.get(7);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "string");
        Assert.assertEquals(actual.getType(), TagType.TAG_STRING);
        Assert.assertEquals(actual.getValue(), tagString.getValue());
        actual = tags.get(8);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "list");
        Assert.assertEquals(actual.getType(), TagType.TAG_LIST);
        tagList = (TagList) actual;
        Assert.assertEquals(tagList.getListType(), TagType.TAG_BYTE);
        List<Tag<?>> loadedTags = tagList.getValue();
        Assert.assertEquals(loadedTags.size(), 100);
        for (int i = 0; i < 100; i++)
        {
            Assert.assertEquals(loadedTags.get(i).getValue(), (byte) i);
        }
        actual = tags.get(9);
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getName(), "long");
        Assert.assertEquals(actual.getType(), TagType.TAG_LONG);
        Assert.assertEquals(actual.getValue(), tagLong.getValue());
    }
}
