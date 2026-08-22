export const hoy = () => new Date().toISOString().slice(0, 10);
export const numero = (n) => n === null || n === undefined ? '—' : n.toLocaleString('es-CO');
export const NOMBRE_VARIANTE = {
    rejilla: 'Rejilla como el papel',
    guiada: 'Guiada, una a la vez',
};
