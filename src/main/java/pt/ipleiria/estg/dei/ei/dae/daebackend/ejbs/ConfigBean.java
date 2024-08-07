package pt.ipleiria.estg.dei.ei.dae.daebackend.ejbs;

import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.annotation.PostConstruct;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.LogisticOperator;
import pt.ipleiria.estg.dei.ei.dae.daebackend.entities.Order;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.OrderDelivery;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PackageType;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.PaymentMethod;
import pt.ipleiria.estg.dei.ei.dae.daebackend.enums.SensorStatus;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyConstraintViolationException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityExistsException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyEntityNotFoundException;
import pt.ipleiria.estg.dei.ei.dae.daebackend.exceptions.MyIllegalArgumentException;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import java.util.logging.Logger;

import static org.jboss.resteasy.util.DateUtil.parseDate;

@Startup
@Singleton
public class ConfigBean {
    @EJB
    private EndConsumerBean endConsumerBean;
    @EJB
    private LogisticOperatorBean logisticOperatorBean;
    @EJB
    private ManufacturerBean manufacturerBean;
    @EJB
    private AdminBean adminBean;
    @EJB
    private ProductBean productBean;
    @EJB
    private CatalogBean catalogBean;
    @EJB
    private PackageBean packageBean;
    @EJB
    private SensorBean sensorBean;
    @EJB
    private MeasurementBean measurementBean;
    @EJB
    private OrderBean orderBean;

    private static final Logger logger = Logger.getLogger("ejbs.ConfigBean");

    @PostConstruct
    public void populateDB() {
        try {
            endConsumerBean.create("ec1", "123", "João Dias", "endConsumer_1@mail.pt", "Rua 1", "123456789");
            endConsumerBean.create("ec2", "123", "Pedro Fonseca", "endConsumer_2@mail.pt", "Rua 2", "987654321");
            endConsumerBean.create("ec3", "123", "Margarida Araújo", "endConsumer_3@mail.pt", "Rua 3", "987654322");
            endConsumerBean.create("ec4", "123", "Rolando Miragaia", "endConsumer_4@mail.pt", "Rua 4", "987654323");
            endConsumerBean.create("ec5", "123", "Carlos Grilo", "endConsumer_5@mail.pt", "Rua 5", "987654324");
            endConsumerBean.create("ec6", "123", "Amália Rodrigues", "endConsumer_6@mail.pt", "Rua 6", "987654325");
            endConsumerBean.create("ec7", "123", "Amélia Graça", "endConsumer_7@mail.pt", "Rua 7", "987654326");

        } catch (MyConstraintViolationException | MyEntityExistsException e) {
        logger.severe(e.getMessage());
        }

        try {
            logisticOperatorBean.create("lo1", "123", "Transportes Fonseca", "logisticOperator_1@mail.pt", "Fonseca LDA", "Camião", "AA-00-AA", "987654321");
            logisticOperatorBean.create("lo2", "123", "Transportes Sempre Abrir", "logisticOperator_2@mail.pt", "CTT", "Carrinha", "BB-00-BB", "123456789");
            logisticOperatorBean.create("lo3", "123", "Frota Navegadores", "logisticOperator_3@mail.pt", "TAP", "Avião", "XX-00-XX", "909999111");

        } catch (MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }
        try {
            manufacturerBean.create("m1", "123", "APPLE", "manufacturer_1@mail.pt", "Tecnologia", "Leiria", "123456789");
            manufacturerBean.create("m2", "123", "SONY", "manufacturer_2@mail.pt", "Tecnologia", "Lisbon", "987654321");
            manufacturerBean.create("m3", "123", "TALHO RATO", "manufacturer_3@mail.pt", "Produtos Alimentares", "Pombal", "987654221");
            manufacturerBean.create("m4", "123", "LIDL", "manufacturer_4@mail.pt", "Produtos Diversos", "Porto", "983374321");
        } catch (MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }

        try{
            catalogBean.create(111, "TVs", "m1");
            catalogBean.create(112, "Computers", "m1");
            catalogBean.create(113, "Phones", "m1");

            catalogBean.create(221, "Televisions", "m2");
            catalogBean.create(222, "PCS", "m2");
            catalogBean.create(223, "Sound", "m2");

            catalogBean.create(331, "Burgers", "m3");
            catalogBean.create(332, "Stakes", "m3");
            catalogBean.create(333, "Dog Meat", "m3");

            catalogBean.create(441, "Clothing", "m4");
            catalogBean.create(442, "Sweets", "m4");
            catalogBean.create(443, "Plants", "m4");

        } catch (MyEntityNotFoundException | MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }

        try {
            productBean.create(1111, "Apple TV", 170.0, 1.0, 111);
            productBean.create(1112, "Apple TV Remote Control", 10.0, 0.2, 111);
            productBean.create(1121, "Macbook Pro", 1500.0, 1.4, 112);
            productBean.create(1122, "Macbook Air", 1100.0, 1.1, 112);
            productBean.create(1131, "iPhone 11", 700.0, 0.2, 113);
            productBean.create(1132, "iPhone 12", 700.0, 0.2, 113);

            productBean.create(2211, "TV XHVD", 2300.0, 8.0, 221);
            productBean.create(2212, "Universal Remote Control", 8.0, 0.2, 221);
            productBean.create(2221, "Vaio", 1900.0, 1.4, 222);
            productBean.create(2222, "Vaio Pro ZRDF", 1200.0, 1.1, 222);
            productBean.create(2231, "Buds XDFR", 69.0, 0.1, 223);
            productBean.create(2232, "Turntable KJS", 160.0, 1.3, 223);

            productBean.create(3311, "Carne de Cão", 0.2, 0.1, 331);
            productBean.create(3312, "Carne de Gato", 0.1, 0.1, 331);
            productBean.create(3321, "Carne de Zebra", 0.5, 0.3, 332);
            productBean.create(3322, "Carne de Antílope", 0.6, 0.4, 332);
            productBean.create(3331, "Carne de Vaca", 0.2, 0.1, 333);
            productBean.create(3332, "Carne de Porco", 0.1, 0.2, 333);

            productBean.create(4411, "Casaco Cabedal", 46.0, 1.6, 441);
            productBean.create(4412, "Boxer Pack 3", 9.0, 1.0, 441);
            productBean.create(4421, "Gomas", 0.5, 0.3, 442);
            productBean.create(4422, "Chocolates", 2.3, 0.4, 442);
            productBean.create(4431, "Cactos", 2.3, 0.6, 443);
            productBean.create(4432, "Orquídeas", 4.2, 1.3, 443);

            packageBean.create(PackageType.PRIMARY, "Cartão do bom", 1, 1.0);
            packageBean.create(PackageType.PRIMARY, "Cartão mais ao menos", 1, 1.0);
            packageBean.create(PackageType.PRIMARY, "Cartão assim assim", 1, 5.0);
            packageBean.create(PackageType.PRIMARY, "Cartão excelente", 1, 5.0);
            packageBean.create(PackageType.PRIMARY, "Cartão dobrável", 1, 10.0);
            packageBean.create(PackageType.PRIMARY, "Cartão inflexível", 1, 10.0);

            packageBean.addProductToPackage(1111,1);
            packageBean.addProductToPackage(1112, 2);
            packageBean.addProductToPackage(1121, 3);
            packageBean.addProductToPackage(1122, 4);
            packageBean.addProductToPackage(1131, 5);
            packageBean.addProductToPackage(1132, 6);

            packageBean.create(PackageType.PRIMARY, "Plático do bom", 1, 2.0);
            packageBean.create(PackageType.PRIMARY, "Plático mais ao menos", 1, 2.0);
            packageBean.create(PackageType.PRIMARY, "Plático assim assim", 1, 6.0);
            packageBean.create(PackageType.PRIMARY, "Plático excelente", 1, 6.0);
            packageBean.create(PackageType.PRIMARY, "Plático dobrável", 1, 11.0);
            packageBean.create(PackageType.PRIMARY, "Plático flexível", 1, 11.0);

            packageBean.addProductToPackage(2211,7);
            packageBean.addProductToPackage(2212, 8);
            packageBean.addProductToPackage(2221, 9);
            packageBean.addProductToPackage(2222, 10);
            packageBean.addProductToPackage(2231, 11);
            packageBean.addProductToPackage(2232, 12);

            packageBean.create(PackageType.PRIMARY, "Metal do bom", 1, 3.0);
            packageBean.create(PackageType.PRIMARY, "Metal mais ao menos", 1, 3.0);
            packageBean.create(PackageType.PRIMARY, "Metal assim assim", 1, 4.0);
            packageBean.create(PackageType.PRIMARY, "Metal excelente", 1, 4.0);
            packageBean.create(PackageType.PRIMARY, "Metal dobrável", 1, 12.0);
            packageBean.create(PackageType.PRIMARY, "Metal flexível", 1, 12.0);

            packageBean.addProductToPackage(3311,13);
            packageBean.addProductToPackage(3312, 14);
            packageBean.addProductToPackage(3321, 15);
            packageBean.addProductToPackage(3322, 16);
            packageBean.addProductToPackage(3331, 17);
            packageBean.addProductToPackage(3332, 18);

            packageBean.create(PackageType.PRIMARY, "Ferro do bom", 1, 2.0);
            packageBean.create(PackageType.PRIMARY, "Ferro mais ao menos", 1, 3.0);
            packageBean.create(PackageType.PRIMARY, "Ferro assim assim", 1, 4.0);
            packageBean.create(PackageType.PRIMARY, "Ferro excelente", 1, 4.0);
            packageBean.create(PackageType.PRIMARY, "Ferro dobrável", 1, 12.0);
            packageBean.create(PackageType.PRIMARY, "Ferro flexível", 1, 12.0);

            packageBean.addProductToPackage(4411,19);
            packageBean.addProductToPackage(4412, 20);
            packageBean.addProductToPackage(4421, 21);
            packageBean.addProductToPackage(4422, 22);
            packageBean.addProductToPackage(4431, 23);
            packageBean.addProductToPackage(4432, 24);

        } catch (MyEntityNotFoundException | MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }
        try {
            packageBean.create(PackageType.SECONDARY, "Cartão", 14, 30.0);
            packageBean.create(PackageType.TERTIARY, "Plático", 15, 20.0);
            packageBean.create(PackageType.SECONDARY, "Metal", 16, 10.0);
            packageBean.create(PackageType.TERTIARY, "Ferro", 17, 40.0);

        } catch (MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }
        try {
            sensorBean.create("Sensor 1", "Leiria", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 2", "Pombal", new Date(), SensorStatus.INACTIVE);
            sensorBean.create("Sensor 3", "Lisboa", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 4", "Porto", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 5", "Caldas da Raínha", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 6", "Cova da Moura", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 7", "Porto de Mós", new Date(), SensorStatus.INACTIVE);
            sensorBean.create("Sensor 8", "Ourem", new Date(), SensorStatus.ACTIVE);
            sensorBean.create("Sensor 9", "Marinha Grande", new Date(), SensorStatus.ACTIVE);


        } catch (MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }

        try {
            measurementBean.create("Temperature", 20.0, "ºC", 1);
            measurementBean.create("Humidity", 50.0, "%", 1);
            measurementBean.create("Temperature", 25.0, "ºC", 2);
            measurementBean.create("Humidity", 60.0, "%", 2);
            measurementBean.create("Damaged", 0, "Boolean", 1);
        } catch (MyEntityNotFoundException | MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }

        try {
            packageBean.enrollSensorInPackage(1, 1);
            packageBean.enrollSensorInPackage(2, 2);
            packageBean.enrollSensorInPackage(2, 3);
            packageBean.enrollSensorInPackage(4, 4);
            packageBean.enrollSensorInPackage(4, 5);
            packageBean.enrollSensorInPackage(5, 6);
            packageBean.enrollSensorInPackage(6, 7);
            packageBean.enrollSensorInPackage(8, 8);
            packageBean.enrollSensorInPackage(8, 9);
        } catch (MyEntityNotFoundException e) {
            logger.severe(e.getMessage());
        }
        try {
            orderBean.create(PaymentMethod.CREDIT_CARD, "ec1");
            orderBean.create(PaymentMethod.BANK_TRANSFER, "ec2");
            orderBean.create(PaymentMethod.PAYPAL, "ec3");
            orderBean.create(PaymentMethod.DEBIT_CARD, "ec4");
            orderBean.create(PaymentMethod.CREDIT_CARD, "ec5");
            orderBean.create(PaymentMethod.CREDIT_CARD, "ec6");

            orderBean.addProductToOrder(1, 1111);
            orderBean.addProductToOrder(1, 2222);

            orderBean.addProductToOrder(2, 4422);
            orderBean.addProductToOrder(2, 4432);

            orderBean.addProductToOrder(3, 3321);

            orderBean.addProductToOrder(4, 4431);
            orderBean.addProductToOrder(4, 1112);
            orderBean.addProductToOrder(4, 4421);

            orderBean.addProductToOrder(5, 3332);
            orderBean.addProductToOrder(5, 3331);

            orderBean.addProductToOrder(6, 3322);
            orderBean.addProductToOrder(6, 2232);

        } catch (MyEntityNotFoundException | MyConstraintViolationException | MyEntityExistsException e) {
            logger.severe(e.getMessage());
        }
        try {
            orderBean.addLogicticOperatorToOrder(1, "lo1");
            orderBean.addLogicticOperatorToOrder(3, "lo3");
            orderBean.addLogicticOperatorToOrder(4, "lo3");
        } catch (MyEntityNotFoundException | MyConstraintViolationException e) {
            logger.severe(e.getMessage());
        }

        try {
            orderBean.setDeliveryStatus(3, OrderDelivery.SHIPPED);
        } catch (MyEntityNotFoundException | MyConstraintViolationException | MyIllegalArgumentException e) {
            logger.severe(e.getMessage());
        }

    }
}
