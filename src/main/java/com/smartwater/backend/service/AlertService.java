package com.smartwater.backend.service;

import com.smartwater.backend.dto.WaterReadingRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlertService {


    private static final double MIN_PH = 6.5;
    private static final double MAX_PH = 8.5;
    private static final double MAX_TURBIDITY = 5.0; // NTU

    public Map<String, Object> evaluateReading(WaterReadingRequest reading) {

        boolean phOut = false;
        boolean turbOut = false;

        double phDelta = 0.0;
        double turbExcess = 0.0;

        StringBuilder message = new StringBuilder();


        if (reading.getPh() != null) {
            double ph = reading.getPh();
            if (ph < MIN_PH || ph > MAX_PH) {
                phOut = true;

                if (ph < MIN_PH) {
                    phDelta = MIN_PH - ph;
                } else {
                    phDelta = ph - MAX_PH;
                }
                message.append("pH level is outside the safe range. ");
            }
        }


        if (reading.getTurbidity() != null) {
            double turbidity = reading.getTurbidity();
            if (turbidity > MAX_TURBIDITY) {
                turbOut = true;
                turbExcess = turbidity - MAX_TURBIDITY;
                message.append("Turbidity is too high. ");
            }
        }


        boolean alert = phOut || turbOut;


        String severity;
        String status;

        if (!alert) {
            severity = "SAFE";
            status = "SAFE";
        } else {
            int violations = 0;
            if (phOut) violations++;
            if (turbOut) violations++;


            if (violations == 1 && phDelta < 1.0 && turbExcess < 2.0) {

                severity = "LOW";
            } else if (violations == 1) {

                severity = "MEDIUM";
            } else {

                severity = "HIGH";
            }

            status = "POLLUTED";
        }


        Map<String, Object> result = new HashMap<>();
        result.put("alert", alert);
        result.put("status", status);       // SAFE / POLLUTED
        result.put("severity", severity);   // SAFE / LOW / MEDIUM / HIGH
        result.put("message", message.toString().trim());

        return result;
    }
}
