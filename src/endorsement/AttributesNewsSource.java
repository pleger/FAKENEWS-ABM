package endorsement;

import inputManager.Configuration;

import java.util.ArrayList;

public class AttributesNewsSource extends Attributes {

    public AttributesNewsSource(ArrayList<String> names, ArrayList<Double[]> values) {
        super(names, values);
    }

    public AttributesNewsSource copy() {
        return new AttributesNewsSource(new ArrayList<>(this.names), new ArrayList<>(this.values));
    }

    public AttributesNewsSource replace(String name, Double[] newValues) {
        ArrayList<String> resultNames = new ArrayList<>();
        ArrayList<Double[]> resultValues = new ArrayList<>();

        forEach((attrName, attrValues) -> {
            resultNames.add(attrName);
            if (attrName.equals(name)) {
                resultValues.add(newValues);
            } else {
                resultValues.add(attrValues);
            }
        });

        return new AttributesNewsSource(resultNames, resultValues);
    }

    public AttributesNewsSource replaceAll(String[] names, Double[][] newValues) {
        AttributesNewsSource result = copy();
        for (int i = 0; i < names.length; ++i) {
            result = result.replace(names[i], newValues[i]);
        }
        return result;
    }

    public AttributesNewsSource replaceAll(String[] names, AttributesNewsSource attm) {
        Double[][] values = new Double[names.length][Configuration.LEVELS];

        for (int i = 0; i < names.length; ++i) {
            values[i] = attm.getValues(names[i]);
        }
        return replaceAll(names, values);
    }
}
