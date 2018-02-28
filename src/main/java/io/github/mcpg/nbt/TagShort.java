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

public class TagShort extends Tag<Short>
{
    /**
     * Creates new instance of TagShort class.
     * @param name tag's name
     * @param value tag's value
     */
    public TagShort(String name, short value)
    {
        super(name, value, TagType.TAG_SHORT);
    }

    /**
     * Creates new instance of TagShort class without setting new value.
     * @param name tag's name
     */
    public TagShort(String name)
    {
        super(name);
        this.type = TagType.TAG_SHORT;
    }

    @Override
    public void writeTagPayload(DataOutputStream outputStream) throws IOException
    {
        outputStream.writeShort(value);
    }

    @Override
    public void readTagPayload(DataInputStream inputStream) throws IOException
    {
        value = inputStream.readShort();
    }
}
