package game.demo.dto;

import java.math.BigDecimal;

public class MembershipPlanView {

    private Long id;
    private String planCode;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String periodLabel;
    private String savingsText;
    private boolean recommended;
    private boolean enabled;
    private int sortOrder;
    private BigDecimal equivalentMonthlyPrice;
    private String computedSavingsText;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanCode() {
        return planCode;
    }

    public void setPlanCode(String planCode) {
        this.planCode = planCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public String getSavingsText() {
        return savingsText;
    }

    public void setSavingsText(String savingsText) {
        this.savingsText = savingsText;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public BigDecimal getEquivalentMonthlyPrice() {
        return equivalentMonthlyPrice;
    }

    public void setEquivalentMonthlyPrice(BigDecimal equivalentMonthlyPrice) {
        this.equivalentMonthlyPrice = equivalentMonthlyPrice;
    }

    public String getComputedSavingsText() {
        return computedSavingsText;
    }

    public void setComputedSavingsText(String computedSavingsText) {
        this.computedSavingsText = computedSavingsText;
    }

    public String getDisplaySavingsText() {
        if (computedSavingsText != null && !computedSavingsText.isBlank()) {
            return computedSavingsText;
        }
        return savingsText;
    }
}
