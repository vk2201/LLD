package advance.lld.strategy;

import advance.lld.model.Denomination;
import com.sun.jdi.request.InvalidRequestStateException;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GreedyDispenseStrategy implements DispenseStrategy{

    @Override
    public Map<Denomination, Long> calculate(BigDecimal amount, Map<Denomination, Long> inventory) {

        Denomination[] denominations = Denomination.values();
        long remaining  = amount.longValue();
        Map<Denomination, Long> requiredDenominationMap = new EnumMap<>(Denomination.class);
        for(Denomination denomination : denominations) {
            if(remaining > denomination.getValue()) {
                long reqCount = remaining/denomination.getValue();
                Long availableCount = inventory.getOrDefault(denomination, 0L);
                long usedCount = Math.min(availableCount, reqCount);
                requiredDenominationMap.put(denomination, usedCount);
                remaining = remaining - (usedCount * denomination.getValue());
            }
            if (remaining == 0L) break;
        }

        if (remaining!= 0L) {
            throw new InvalidRequestStateException("cannot dispense this amount");
        }
        return requiredDenominationMap;
    }
}
