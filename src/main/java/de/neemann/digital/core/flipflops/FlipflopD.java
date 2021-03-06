package de.neemann.digital.core.flipflops;

import de.neemann.digital.core.*;
import de.neemann.digital.core.element.Element;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.ElementTypeDescription;
import de.neemann.digital.core.element.Keys;

import static de.neemann.digital.core.ObservableValues.ovs;
import static de.neemann.digital.core.element.PinInfo.input;

/**
 * The D Flipflop
 *
 * @author hneemann
 */
public class FlipflopD extends Node implements Element {

    /**
     * The D-FF description
     */
    public static final ElementTypeDescription DESCRIPTION
            = new ElementTypeDescription("D_FF", FlipflopD.class, input("D"), input("C").setClock())
            .addAttribute(Keys.ROTATE)
            .addAttribute(Keys.BITS)
            .addAttribute(Keys.LABEL)
            .addAttribute(Keys.DEFAULT)
            .addAttribute(Keys.INVERTER_CONFIG)
            .addAttribute(Keys.VALUE_IS_PROBE);

    private final int bits;
    private final boolean isProbe;
    private final String label;
    private ObservableValue dVal;
    private ObservableValue clockVal;
    private ObservableValue q;
    private ObservableValue qn;
    private boolean lastClock;
    private long value;

    /**
     * Creates a new instance
     *
     * @param attributes the attributes
     */
    public FlipflopD(ElementAttributes attributes) {
        this(attributes,
                new ObservableValue("Q", attributes.getBits()).setPinDescription(DESCRIPTION),
                new ObservableValue("~Q", attributes.getBits()).setPinDescription(DESCRIPTION));
    }

    /**
     * Creates a new D-FF with the given outputs!
     *
     * @param label the label
     * @param q     output
     * @param qn    inverted output
     */
    public FlipflopD(String label, ObservableValue q, ObservableValue qn) {
        this(new ElementAttributes().set(Keys.LABEL, label).setBits(q.getBits()), q, qn);
        if (qn.getBits() != q.getBits())
            throw new RuntimeException("wrong bit count given!");
    }

    FlipflopD(ElementAttributes attributes, ObservableValue q, ObservableValue qn) {
        super(true);
        bits = attributes.getBits();
        this.q = q;
        this.qn = qn;
        isProbe = attributes.get(Keys.VALUE_IS_PROBE);
        label = attributes.getCleanLabel();

        value = attributes.get(Keys.DEFAULT);
        q.setValue(value);
        qn.setValue(~value);
    }

    @Override
    public void readInputs() throws NodeException {
        boolean clock = clockVal.getBool();
        if (clock && !lastClock)
            value = dVal.getValue();
        lastClock = clock;
    }

    @Override
    public void writeOutputs() throws NodeException {
        q.setValue(value);
        qn.setValue(~value);
    }

    @Override
    public void setInputs(ObservableValues inputs) throws BitsException {
        dVal = inputs.get(0).checkBits(bits, this, 0);
        clockVal = inputs.get(1).addObserverToValue(this).checkBits(1, this, 1);
    }

    @Override
    public ObservableValues getOutputs() {
        return ovs(q, qn);
    }

    @Override
    public void registerNodes(Model model) {
        super.registerNodes(model);
        if (isProbe)
            model.addSignal(new Signal(label, q, (v, z) -> {
                value = v;
                q.setValue(value);
                qn.setValue(~value);
            }));
    }

    /**
     * @return the D input
     */
    public ObservableValue getDInput() {
        return dVal;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the clock value
     */
    public ObservableValue getClock() {
        return clockVal;
    }

    /**
     * @return number of bits
     */
    public int getBits() {
        return bits;
    }

    void setValue(long value) {
        this.value = value;
    }
}
