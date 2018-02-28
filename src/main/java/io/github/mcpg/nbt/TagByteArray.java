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

public class TagByteArray extends Tag<byte[]>
{
    /**
     * Creates new instance of TagByteArray class.
     * @param name tag's name
     * @param value tag's value
     */
    public TagByteArray(String name, byte[] value)
    {
        super(name, value, TagType.TAG_BYTE_ARRAY);
    }

    /**
     * Creates new instance of TagByteArray class without setting value.
     * @param name tag's name
     */
    public TagByteArray(String name)
    {
        super(name);
        this.type = TagType.TAG_BYTE_ARRAY;
    }

    @Override
    public void writeTagPayload(DataOutputStream outputStream) throws IOException
    {
        outputStream.writeInt(value.length);
        outputStream.write(value);
    }

    @Override
    public void readTagPayload(DataInputStream inputStream) throws IOException
    {
        byte[] newValue = new byte[inputStream.readInt()];
        inputStream.readFully(newValue);
        value = newValue;
    }
}
