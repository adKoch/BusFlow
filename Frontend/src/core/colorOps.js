const minSaturation = 0.2;
const minLightness = 0.2;

function asHex(num) {
    return Math.trunc(num).toString(16)
}

function hsvToRgb(h, x, c, m) {
    const cmHex = asHex((c + m) * 255);
    const xmHex = asHex((x + m) * 255);

    if (h <= 1) return `#${cmHex}${xmHex}00`;
    else if (h <= 2) return `#${xmHex}${cmHex}00`;
    else if (h <= 3) return `#00${cmHex}${xmHex}`;
    else if (h <= 4) return `#00${xmHex}${cmHex}`;
    else if (h <= 5) return `#${xmHex}00${cmHex}`;
    else return `#${cmHex}00${xmHex}`;
}

function partToRGB(value, maxValue) {
    const lightness = 1 - value / maxValue < minLightness ? minLightness : 1 - value / maxValue;
    const saturation = value / maxValue < minSaturation ? minSaturation : value / maxValue;
    const h = 6 * value / maxValue;
    const c = (1 - Math.abs(2 * lightness - 1)) * saturation;
    const x = c * (1 - Math.abs(h % 2 - 1));
    const m = lightness - c / 2;
    return hsvToRgb(h, x, c, m)
}

export function partAsHexColor(value, maxValue) {
    return partToRGB(value, maxValue)
}
