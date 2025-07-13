package com.codeflow.toolflow.dto.transfer.validation;

import com.codeflow.toolflow.dto.transfer.TransferRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class AtLeastOneItemValidator implements ConstraintValidator<AtLeastOneItemRequired, TransferRequest> {

    @Override
    public boolean isValid(TransferRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true; // Let other validators handle the null case
        }

        List<?> tools = request.getTools();
        List<?> parts = request.getVehicleParts();
        List<?> vehicles = request.getVehicles();

        boolean hasTools = tools != null && !tools.isEmpty();
        boolean hasParts = parts != null && !parts.isEmpty();
        boolean hasVehicles = vehicles != null && !vehicles.isEmpty();

        // The request is valid if at least one of the lists is not empty
        return hasTools || hasParts || hasVehicles;
    }
}
