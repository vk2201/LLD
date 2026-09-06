package advance.lld.model;

public class ServiceChargeDecorator extends BillDecorator {

    private final double servicePercentage;

    public ServiceChargeDecorator(BillComponent billComponent, double servicePercentage) {
        super(billComponent);
        this.servicePercentage = servicePercentage;
    }

    @Override
    public Double calculate() {
        return billComponent.calculate() + servicePercentage/100;
    }
}
