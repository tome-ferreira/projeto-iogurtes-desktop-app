//v1.0
package com.gestaoiogurtes.bll.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Superclasse de todas as entidades do BLL.
 * <p>
 * Todos os campos definidos aqui estão presentes em <b>todos</b> os modelos.
 * Utiliza soft-delete — nunca apaga registos; marca {@code isActive = false}.
 * </p>
 */
public abstract class BaseEntity {

    private UUID id;
    /** {@code true} enquanto o registo está activo; {@code false} após soft-delete. */
    private boolean isActive = true;
    /** Data/hora do soft-delete; {@code null} enquanto activo. */
    private LocalDateTime deletedAt;
    /** Preenchido automaticamente no momento da criação. */
    private LocalDateTime createdAt;
    /** Actualizado automaticamente em cada alteração. */
    private LocalDateTime updatedAt;

    protected BaseEntity() {
        this.id        = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters / setters ──────────────────────────────────────────

    public UUID getId()                    { return id; }
    public void setId(UUID id)             { this.id = id; }

    public boolean isActive()              { return isActive; }
    public void setActive(boolean active)  { this.isActive = active; }

    public LocalDateTime getDeletedAt()    { return deletedAt; }
    public void setDeletedAt(LocalDateTime d) { this.deletedAt = d; }

    public LocalDateTime getCreatedAt()    { return createdAt; }
    public void setCreatedAt(LocalDateTime c) { this.createdAt = c; }

    public LocalDateTime getUpdatedAt()    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u) { this.updatedAt = u; }

    /**
     * Executa o soft-delete: marca o registo como inactivo e regista
     * o instante da eliminação.
     */
    public void softDelete() {
        this.isActive  = false;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
