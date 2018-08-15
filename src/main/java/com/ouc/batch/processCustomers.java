package com.ouc.batch;

import com.ouc.model.Customer;
import com.ouc.utils.ConfigProperties;
import org.beanio.BeanWriter;

import java.util.List;
import java.util.stream.Collectors;

public class processCustomers {

    public List<Customer> execute(List<Object> objects, ConfigProperties props) {
        List<Customer> customers = objects.stream().filter(object -> object instanceof Customer).map(object -> (Customer) object).collect(Collectors.toList());
        //BeanWriter writer = new Writer("output.dir", "customers.json", "out.customer.mapping", "customers", props).getWriter();
        //writeCustomers(customers, writer);

        return customers;
    }


    private void writeCustomers(List<Customer> customers, BeanWriter writer) {
        customers.forEach(writer::write);
        writer.flush();
        writer.close();
    }
}
