package de.neemann.digital.core.basic;

import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import java.util.ArrayList;

/**
 * The Or
 * @author hneemann
 */
public class XOr extends Function {

    /**
     * The And description
     */
    public static final ElementTypeDescription DESCRIPTION = new FanInDescription(XOr.class);

    /**
     * Creates a new instance
     *
     * @param attributes the attributes
     */
    public XOr(ElementAttributes attributes) {
        super(attributes.get(Keys.BITS));
    }

    @Override
    protected int calculate(ArrayList<ObservableValue> inputs) throws NodeException {
        int f = 0;
        for (ObservableValue i : inputs) {
            f ^= i.getValue();
        }
        return f;
    }
}
