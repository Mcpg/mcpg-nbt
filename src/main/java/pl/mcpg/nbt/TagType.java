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

package pl.mcpg.nbt;

public enum TagType
{
    TAG_END("TAG_End", 0, TagEnd.class),
    TAG_BYTE("TAG_Byte", 1, TagByte.class),
    TAG_SHORT("TAG_Short", 2, TagShort.class),
    TAG_INT("TAG_Int", 3, TagInt.class),
    TAG_LONG("TAG_Long", 4, TagLong.class),
    TAG_FLOAT("TAG_Float", 5, TagFloat.class),
    TAG_DOUBLE("TAG_Double", 6, TagDouble.class),
    TAG_BYTE_ARRAY("TAG_Byte_Array", 7, TagByteArray.class),
    TAG_STRING("TAG_String", 8, TagString.class),
    TAG_LIST("TAG_List", 9, TagList.class),
    TAG_COMPOUND("TAG_Compound", 10, TagCompound.class),
    TAG_INT_ARRAY("TAG_Int_Array", 11, TagIntArray.class),
    TAG_LONG_ARRAY("TAG_Long_Array", 12, TagLongArray.class);

    private final String name;
    private final int id;
    private final Class<? extends Tag<?>> clazz;

    TagType(String name, int id, Class<? extends Tag<?>> clazz)
    {
        this.name = name;
        this.id = id;
        this.clazz = clazz;
    }

    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public Class<? extends Tag<?>> getClazz()
    {
        return clazz;
    }

    public static TagType byId(int id)
    {
        for (TagType type : values())
        {
            if (type.id == id)
            {
                return type;
            }
        }
        return null;
    }
}
