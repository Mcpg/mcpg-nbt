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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.github.mcpg.nbt.TagCompound.CompoundContent;

public class TagCompound extends Tag<CompoundContent>
{
    /**
     * Creates new instance of TagCompound class.
     * @param name tag's name
     * @param value tag's value
     */
    public TagCompound(String name, CompoundContent value)
    {
        super(name, value, TagType.TAG_COMPOUND);
    }

    /**
     * Creates new instance of TagByte class without setting value.
     * @param name tag's name
     */
    public TagCompound(String name)
    {
        super(name);
        this.type = TagType.TAG_COMPOUND;
    }

    @Override
    public void writeTagPayload(DataOutputStream outputStream) throws IOException
    {
        for (Tag<?> tag : value.getTagsList())
        {
            tag.writeTag(outputStream);
        }
        new TagEnd().writeTag(outputStream);
    }

    @Override
    public void readTagPayload(DataInputStream inputStream) throws IOException
    {
        if (value == null)
        {
            value = new CompoundContent();
        }
        else
        {
            value.getTagsList().clear();
        }

        TagType type;
        while (true)
        {
            int id = inputStream.readUnsignedByte();
            type = TagType.byId(id);
            if (type == null)
            {
                throw new IllegalStateException("Detected unknown tag with ID " + id + "!");
            }
            if (type == TagType.TAG_END)
            {
                break;
            }
            byte[] rawName = new byte[inputStream.readUnsignedShort()];
            inputStream.readFully(rawName);
            if (type.getClazz() == null)
            {
                throw new IllegalStateException("Type " + type + " has got null clazz!");
            }
            Object obj;
            try
            {
                Constructor<?> constructor = type.getClazz().getConstructor(String.class);
                obj = constructor.newInstance(new String(rawName, StandardCharsets.UTF_8));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e)
            {
                throw new IllegalStateException("Couldn't create object from type " + type + "!", e);
            }
            if (!(obj instanceof Tag<?>))
            {
                throw new IllegalStateException("Created object isn't Tag<?>!");
            }
            Tag<?> tag = (Tag<?>) obj;
            tag.readTagPayload(inputStream);
            value.addTag(tag);
        }
    }

    public static class CompoundContent
    {
        private List<Tag<?>> tags;

        /**
         * Creates new CompoundComponent instance.
         */
        public CompoundContent()
        {
            this.tags = new ArrayList<>();
        }

        /**
         * Returns tags list for this compound. For getting values see {@link #getTag(String)},
         * and other methods in this class
         * @return tags list
         */
        public List<Tag<?>> getTagsList()
        {
            return tags;
        }

        /**
         * Gets tag from the compound content. If none found, null is returned
         * @param name tag's name
         * @return tag from compound content, or null if not found
         * @throws NullPointerException if name is null
         */
        public Tag<?> getTag(String name)
        {
            if (name == null)
            {
                throw new NullPointerException("name is null");
            }
            for (Tag<?> tag : tags)
            {
                if (tag.getName().equals(name))
                {
                    return tag;
                }
            }
            return null;
        }

        /**
         * Checks if given tag exists in this compound content.
         * @param name tag's name
         * @return <code>true</code> if found, <code>false</code> otherwise.
         * @throws NullPointerException if name is null
         */
        public boolean containsTag(String name)
        {
            if (name == null)
            {
                throw new NullPointerException("name is null");
            }
            for (Tag<?> tag : tags)
            {
                if (tag.getName().equals(name))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * Adds new tag into this compound content.
         * @param tag tag to add.
         * @throws NullPointerException if tag is null
         * @throws IllegalArgumentException if tag's type is TAG_End
         * @throws IllegalArgumentException if tag with this name is already in the compound content
         */
        public void addTag(Tag<?> tag)
        {
            if (tag == null)
            {
                throw new NullPointerException("tag is null");
            }
            if (tag.getType().equals(TagType.TAG_END))
            {
                throw new IllegalArgumentException("Can't push TAG_End into TAG_Compound!");
            }
            if (containsTag(tag.getName()))
            {
                throw new IllegalArgumentException("Tag with this name already exists!");
            }
            tags.add(tag);
        }
    }
}
