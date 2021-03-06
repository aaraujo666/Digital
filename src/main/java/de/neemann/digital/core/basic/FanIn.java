package de.neemann.digital.core.basic;

import de.neemann.digital.core.Node;
import de.neemann.digital.core.NodeException;
import de.neemann.digital.core.ObservableValue;
import de.neemann.digital.core.ObservableValues;
import de.neemann.digital.core.element.*;
import de.neemann.digital.lang.Lang;

import java.util.ArrayList;

import static de.neemann.digital.core.element.PinInfo.input;

/**
 * A fan in. Used as base class for the simple bool operations
 *
 * @author hneemann
 */
public abstract class FanIn extends Node implements Element {

    /**
     * The inputs name prefix
     */
    public static final String PREFIX = "In_";

    private final ArrayList<ObservableValue> inputs;
    private final ObservableValue output;
    private final int bits;

    /**
     * Creates a new instance
     *
     * @param bits the number of bits
     */
    public FanIn(int bits) {
        this.bits = bits;
        inputs = new ArrayList<>();
        output = new ObservableValue("out", bits).setDescription(Lang.get("elem_Basic_Out"));
    }

    @Override
    public void setInputs(ObservableValues in) throws NodeException {
        for (ObservableValue v : in)
            inputs.add(v.checkBits(bits, this).addObserverToValue(this));
    }

    /**
     * @return the outputs
     */
    public ObservableValue getOutput() {
        return output;
    }

    /**
     * @return the outputs
     */
    public ArrayList<ObservableValue> getInputs() {
        return inputs;
    }

    @Override
    public ObservableValues getOutputs() {
        return output.asList();
    }

    /**
     * The fan in description
     */
    static class FanInDescription extends ElementTypeDescription {
        FanInDescription(Class<? extends Element> clazz) {
            super(clazz);
            addAttributes();
        }

        private void addAttributes() {
            addAttribute(Keys.ROTATE);
            addAttribute(Keys.BITS);
            addAttribute(Keys.INPUT_COUNT);
            addAttribute(Keys.INVERTER_CONFIG);
        }

        @Override
        public PinDescriptions getInputDescription(ElementAttributes elementAttributes) {
            int count = elementAttributes.get(Keys.INPUT_COUNT);
            PinDescription[] names = new PinDescription[count];
            for (int i = 0; i < count; i++)
                names[i] = input(PREFIX + (i + 1), Lang.get("elem_Basic_In", i + 1));
            return new PinDescriptions(names);
        }
    }
}
