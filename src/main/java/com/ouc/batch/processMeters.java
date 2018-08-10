package com.ouc.batch;

import com.ouc.io.Writer;
import com.ouc.utils.ConfigProperties;
import com.ouc.model.Meter;
import org.beanio.BeanWriter;

import java.util.List;
import java.util.stream.Collectors;

public class processMeters {

    public List<Meter> execute(List<Object> objects, ConfigProperties props) {
        List<Meter> meters = objects.stream().filter(object -> object instanceof Meter).map(object -> (Meter) object).collect(Collectors.toList());
        //BeanWriter writer = new Writer("output.dir", "meters.json", "out.meter.mapping", "meters", props).getWriter();
        //writeMeters(meters, writer);

        return meters;
    }


    private void writeMeters(List<Meter> meters, BeanWriter writer) {
        meters.forEach(writer::write);
        writer.flush();
        writer.close();
    }
}
