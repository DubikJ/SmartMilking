package com.avatlantik.smartmilking.milkingStanchion;

import com.avatlantik.smartmilking.milkingStanchion.MilkingStancionParams.ConnectionStatus;
import com.avatlantik.smartmilking.model.MilkingResult;

public interface MilkingStanchionInterface {


    void onStatusChange(ConnectionStatus status);

    void onResult(MilkingResult milkingResult);

    void onWishingAllowed(Boolean allowed);

    void onVacuumPumpStart(Boolean started);

    void onMilkPumpStart(Boolean started);

}
