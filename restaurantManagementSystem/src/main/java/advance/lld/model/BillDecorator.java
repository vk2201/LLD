package advance.lld.model;

public abstract class BillDecorator implements BillComponent {

    protected BillComponent billComponent;

    protected BillDecorator(BillComponent billComponent) {
        this.billComponent = billComponent;
    }

}
