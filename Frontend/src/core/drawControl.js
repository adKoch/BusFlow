function createDrawControl(shapeLayer, figureColor) {
    return new window.L.Control.Draw({
        position: 'bottomleft',
        draw: {
            polyline: false,
            polygon: {
                shapeOptions: {
                    color: figureColor,
                },
            },
            rectangle: {
                shapeOptions: {
                    color: figureColor,
                }
            },
            circle: false,
            circlemarker: false,
            marker: false
        },
        edit: {
            featureGroup: shapeLayer,
            remove: true,
            edit: false,
        }
    });
}

export function getDrawControl(shapeLayer, figureColor) {
    return createDrawControl(shapeLayer, figureColor)
}