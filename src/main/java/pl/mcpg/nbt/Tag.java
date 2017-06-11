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

package pl.mcpg.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Tag class represents an NBT tag. Each tag has got a name, a value and type. Note: if
 * you're extending this class <b>you need</b> to have a constructor for String (name) and
 * and Object (probably T - it's value), because reading tags is based on this constructor.
 *
 * @param <T> what this tag does contain
 */
public abstract class Tag<T>
{
    /**
     * Type of the tag.
     */
    protected TagType type;

    /**
     * Changeable name of the tag.
     */
    protected String name;

    /**
     * Changeable value of the tag.
     */
    protected T value;

    /**
     * Constructor, that created new Tag object, it's also
     * base for dedicated tag classes.
     *
     * @param name  name of the tag
     * @param value value of the tag
     * @param type  type of the tag
     */
    public Tag(String name, T value, TagType type)
    {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    /**
     * Constructor, that should be overridden by all of the Tag-based classes. It's used
     * by {@link TagReadingUtils} to construct a tag with reflection and only name. This
     * constructor should set {@link #value} field to null and {@link #type} to tag's type.
     *
     * @param name name of the tag
     */
    public Tag(String name)
    {
        this.name = name;
        this.value = null;
        this.type = null;
    }

    /**
     * Returns name of the tag.
     *
     * @return name of the tag
     */
    public String getName()
    {
        return name;
    }

    /**
     * Renames the tag. TAG_Compound in this library is based on list, not {@link java.util.Map},
     * so everything should work correctly.
     *
     * @param name new name of the tag
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns value of the tag.
     *
     * @return value of the tag.
     */
    public T getValue()
    {
        return value;
    }

    /**
     * Sets value of the tag.
     *
     * @param value new value of the tag
     */
    public void setValue(T value)
    {
        this.value = value;
    }

    /**
     * Returns type of the tag.
     *
     * @return type of the tag
     */
    public TagType getType()
    {
        return type;
    }

    /**
     * Writes the entire tag into given output stream. Note, that this method calls
     * {@link #writeTagPayload(DataOutputStream)}.
     *
     * @param outputStream output stream to use
     *
     * @throws IOException if any I/O exception occurs
     */
    public void writeTag(DataOutputStream outputStream) throws IOException
    {
        outputStream.writeByte(getType().getId());
        byte[] rawName = name.getBytes(StandardCharsets.UTF_8);
        outputStream.writeShort(rawName.length);
        outputStream.write(rawName);
        writeTagPayload(outputStream);
    }

    /**
     * Writes tag <b>payload</b> (like number, or string) into given output stream.
     * This method is used in {@link #writeTag(DataOutputStream)}.
     *
     * @param outputStream output stream to use
     *
     * @throws IOException if any I/O error occurs
     */
    public abstract void writeTagPayload(DataOutputStream outputStream) throws IOException;

    /**
     * Reads tag payload from given input stream into object, wherever the method is
     * called.
     *
     * @param inputStream input stream to use
     *
     * @throws IOException if any I/O error occurs
     */
    public abstract void readTagPayload(DataInputStream inputStream) throws IOException;

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Tag<?> && ((Tag<?>) obj).getValue().equals(value);
    }
}
