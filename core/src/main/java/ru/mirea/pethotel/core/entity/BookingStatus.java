package ru.mirea.pethotel.core.entity;

public enum BookingStatus {
    PENDING, ACCEPTED, DENIED, CANCELLED, COMPLETED;

    public boolean canTransitionTo(BookingStatus target) {
        switch (this)
        {
            case PENDING:
                return target == ACCEPTED || target == DENIED || target == CANCELLED;
            case ACCEPTED:
                return target == COMPLETED || target == CANCELLED;
            default:
                return false;
        }
    }
}
