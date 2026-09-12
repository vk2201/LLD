package advance.lld.strategy;

import advance.lld.model.Denomination;

import java.math.BigDecimal;
import java.util.Map;

public interface DispenseStrategy {

    public Map<Denomination, Long> calculate(BigDecimal amount, Map<Denomination, Long> inventory);

}
