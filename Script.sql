DELIMITER //

DROP PROCEDURE IF EXISTS sp_estadisticas_actividad//

CREATE PROCEDURE sp_estadisticas_actividad(
    IN p_idActividad VARCHAR(10),
    OUT p_numSocios INT,
    OUT p_edadMedia DOUBLE,
    OUT p_catFrecuente CHAR(1),
    OUT p_ingresos DOUBLE
)
BEGIN
    -- 1. Número de socios
    SELECT COUNT(*) INTO p_numSocios 
    FROM REALIZA 
    WHERE idActividad = p_idActividad;

    -- 2. Edad media
    SELECT IFNULL(AVG(TIMESTAMPDIFF(YEAR, STR_TO_DATE(s.fechaNacimiento, '%d/%m/%Y'), CURDATE())), 0) INTO p_edadMedia
    FROM SOCIO s JOIN REALIZA r ON s.numeroSocio = r.numeroSocio
    WHERE r.idActividad = p_idActividad;

    -- 3. Categoría más frecuente
    SELECT s.categoria INTO p_catFrecuente
    FROM SOCIO s JOIN REALIZA r ON s.numeroSocio = r.numeroSocio
    WHERE r.idActividad = p_idActividad
    GROUP BY s.categoria
    ORDER BY COUNT(*) DESC
    LIMIT 1;

    -- 4. Ingresos
    SELECT IFNULL(SUM(
        a.precioBaseMes * CASE s.categoria
            WHEN 'A' THEN 1.0
            WHEN 'B' THEN 0.9
            WHEN 'C' THEN 0.8
            WHEN 'D' THEN 0.7
            WHEN 'E' THEN 0.6
            ELSE 1.0
        END
    ), 0) INTO p_ingresos
    FROM ACTIVIDAD a
    JOIN REALIZA r ON a.idActividad = r.idActividad
    JOIN SOCIO s ON r.numeroSocio = s.numeroSocio
    WHERE a.idActividad = p_idActividad;

    -- Ajuste final si no hay socios
    IF p_numSocios = 0 THEN
        SET p_catFrecuente = '-';
    END IF;
END//

DELIMITER ;