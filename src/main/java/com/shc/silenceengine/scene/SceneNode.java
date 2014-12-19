package com.shc.silenceengine.scene;

import com.shc.silenceengine.core.SilenceException;
import com.shc.silenceengine.graphics.Batcher;
import com.shc.silenceengine.graphics.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sri Harsha Chilakapati
 */
public class SceneNode
{
    private List<SceneNode> children;
    private SceneNode       parent;
    private Transform       transform;
    private boolean         destroyed;

    public SceneNode()
    {
        children = new ArrayList<>();
        transform = new Transform();
        parent = null;
    }

    public void preInit()
    {
        init();
        initChildren();
    }

    public void init()
    {
    }

    public void initChildren()
    {
        for (int i = 0; i < children.size(); i++)
            children.get(i).preInit();
    }

    public void addChild(SceneNode child)
    {
        if (child.getParent() != null)
            throw new SilenceException("A WorldComponent can be a child of a single parent!");

        children.add(child);
        child.setParent(this);
        child.init();
    }

    public void preUpdate(float delta)
    {
        update(delta);
        updateChildren(delta);
    }

    public void update(float delta)
    {
    }

    public void updateChildren(float delta)
    {
        for (int i = 0; i < children.size(); i++)
        {
            SceneNode child = children.get(i);
            child.preUpdate(delta);

            if (child.isDestroyed())
            {
                removeChild(child);
                i--;
            }
        }
    }

    public void preRender(float delta, Batcher batcher)
    {
        render(delta, batcher);
        renderChildren(delta, batcher);
    }

    public void render(float delta, Batcher batcher)
    {
    }

    public void renderChildren(float delta, Batcher batcher)
    {
        for (int i = 0; i < children.size(); i++)
        {
            SceneNode child = children.get(i);
            child.preRender(delta, batcher);

            if (child.isDestroyed())
            {
                removeChild(child);
                i--;
            }
        }
    }

    public void removeChild(SceneNode child)
    {
        if (child.getParent() != this)
            throw new SilenceException("Cannot remove non-existing Child!");

        child.destroy();
        children.remove(child);
        child.setParent(null);
    }

    public void removeChildren()
    {
        for (int i = 0; i < children.size(); i++)
        {
            removeChild(children.get(i));
            i--;
        }
    }

    public void destroy()
    {
        destroyed = true;
        destroyChildren();
    }

    public void destroyChildren()
    {
        for (int i = 0; i < children.size(); i++)
        {
            SceneNode child = children.get(i);
            child.destroy();
            removeChild(child);
            i--;
        }
    }

    public List<SceneNode> getChildren()
    {
        return children;
    }

    public SceneNode getParent()
    {
        return parent;
    }

    private void setParent(SceneNode parent)
    {
        this.parent = parent;
    }

    public Transform getTransform()
    {
        if (getParent() != null)
            return transform.copy().apply(parent.getTransform());
        else
            return transform;
    }

    public Transform getLocalTransform()
    {
        return transform;
    }

    public boolean isDestroyed()
    {
        return destroyed;
    }
}