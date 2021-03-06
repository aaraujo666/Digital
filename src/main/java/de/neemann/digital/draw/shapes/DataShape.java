package de.neemann.digital.draw.shapes;

import de.neemann.digital.core.Model;
import de.neemann.digital.core.Observer;
import de.neemann.digital.core.Signal;
import de.neemann.digital.core.element.ElementAttributes;
import de.neemann.digital.core.element.Keys;
import de.neemann.digital.core.element.PinDescriptions;
import de.neemann.digital.data.DataPlotter;
import de.neemann.digital.data.Value;
import de.neemann.digital.data.ValueTable;
import de.neemann.digital.draw.elements.IOState;
import de.neemann.digital.draw.elements.Pins;
import de.neemann.digital.draw.graphics.Graphic;
import de.neemann.digital.draw.graphics.Style;
import de.neemann.digital.draw.model.ModelCreator;
import de.neemann.digital.draw.model.ModelEntry;
import de.neemann.digital.gui.components.OrderMerger;
import de.neemann.digital.gui.components.data.ValueTableObserver;

import java.util.ArrayList;

/**
 * Shape which shows the data graph inside the models circuit area.
 *
 * @author hneemann
 */
public class DataShape implements Shape {

    private final boolean microStep;
    private final int maxSize;
    private ValueTable logDataModel;
    private ValueTable logData;

    /**
     * Creates a new instance
     *
     * @param attr    the attributes
     * @param inputs  the inputs
     * @param outputs the outputs
     */
    public DataShape(ElementAttributes attr, PinDescriptions inputs, PinDescriptions outputs) {
        microStep = attr.get(Keys.MICRO_STEP);
        maxSize = attr.get(Keys.MAX_STEP_COUNT);
    }

    @Override
    public Pins getPins() {
        return new Pins();
    }

    @Override
    public Interactor applyStateMonitor(IOState ioState, Observer guiObserver) {
        return null;
    }


    @Override
    public void readObservableValues() {
        if (logDataModel != null)
            logData = new ValueTable(logDataModel);
    }

    @Override
    public void drawTo(Graphic graphic, Style heighLight) {
        if (logData == null) {
            logData = new ValueTable("A", "B", "C")
                    .add(new Value[]{new Value(0), new Value(0), new Value(0)})
                    .add(new Value[]{new Value(0), new Value(1), new Value(0)});
        }
        new DataPlotter(logData).drawTo(graphic, null);
    }

    @Override
    public void registerModel(ModelCreator modelCreator, Model model, ModelEntry element) {
        ArrayList<Signal> signals = model.getSignalsCopy();
        new OrderMerger<String, Signal>(modelCreator.getCircuit().getMeasurementOrdering()) {
            @Override
            public boolean equals(Signal a, String b) {
                return a.getName().equals(b);
            }
        }.order(signals);

        ValueTableObserver valueTableObserver = new ValueTableObserver(microStep, signals, maxSize);
        logDataModel = valueTableObserver.getLogData();
        model.addObserver(valueTableObserver);
    }
}
