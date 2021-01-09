/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.meulensteen.dennis.carbonbal_laptop.control;

import nl.meulensteen.dennis.carbonbal_laptop.model.TimeValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dennis
 */
public class Utils {

    public static List<Double> calculateAverages(List<Double> averages, List<TimeValue<Integer>> intValues) {
        for (int i = 0; i < 4; i++) {
            averages.set(i, averages.get(i) + (intValues.get(i).value - averages.get(i)) / 100);
        }
        return averages;
    }
        
    public static TimeValue[] getAveragedTimeValues(int counter, List<Double> averages) {
        Double now = Double.valueOf(counter);

        TimeValue[] tuples = {new TimeValue(now, averages.get(0)), new TimeValue(now, averages.get(1)),
            new TimeValue(now, averages.get(2)), new TimeValue(now, averages.get(3))};
        return tuples;
    }

    public static List<TimeValue<Integer>> getRawTimeValues(Integer counter, List<Integer> averages) {
        List<TimeValue<Integer>> tuples = new ArrayList(4);
        
        tuples.add(new TimeValue<Integer>(counter, averages.get(0)));
        tuples.add(new TimeValue<Integer>(counter, averages.get(1)));
        tuples.add(new TimeValue<Integer>(counter, averages.get(2)));
        tuples.add(new TimeValue<Integer>(counter, averages.get(3)));
    
        return tuples;
    }
    
    public static List<TimeValue<Double>> getTimeValues(Integer counter, List<Double> averages) {
        Double now = Double.valueOf(counter);

        List<TimeValue<Double>> tuples = new ArrayList<>();
        {
            tuples.add(new TimeValue(now, averages.get(0)));
            tuples.add(new TimeValue(now, averages.get(1)));
            tuples.add(new TimeValue(now, averages.get(2)));
            tuples.add(new TimeValue(now, averages.get(3)));
        };
        return tuples;
    }

    public static List<Integer> parseValues(byte[] numbers) {
        int intValue;

        if (numbers.length < 8) {
            return null;
        }

        List<Integer> values = new ArrayList(4);

        for (int i = 0; i < 8; i++) {
            intValue = ((int) numbers[i]) << 8;
            intValue |= Byte.toUnsignedInt(numbers[i+1]);

            values.add(intValue);
            i++;
        }
        return values;
    }
    
        public static List<Integer> parseCalibrationValues(byte[] numbers) {
        if (numbers.length < 6) {
            return null;
        }

        List<Integer> values = new ArrayList(4);

        for (int i = 2; i < 6; i++) {
            values.add((int) numbers[i]);
        }
        return values;
    }

}
