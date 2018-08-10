package com.ouc.batch;

import com.ouc.io.Writer;
import com.ouc.model.Install;
import com.ouc.utils.ConfigProperties;
import org.beanio.BeanWriter;

import java.util.List;
import java.util.stream.Collectors;

public class processInstalls {

    public List<Install> execute(List<Object> objects, ConfigProperties props) {
        List<Install> installs = objects.stream().filter(object -> object instanceof Install).map(object -> (Install) object).collect(Collectors.toList());
        //BeanWriter writer = new Writer("output.dir", "installs.csv", "out.install.mapping", "installs", props).getWriter();
        //writeMeters(installs, writer);

        return installs;
    }


    private void writeMeters(List<Install> installs, BeanWriter writer) {
        installs.forEach(writer::write);
        writer.flush();
        writer.close();
    }
}
