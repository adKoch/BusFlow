function createDrawLocal() {
    window.L.drawLocal = {
        draw: {
            toolbar: {
                actions: {
                    title: 'Anuluj rysowanie',
                    text: 'Anuluj'
                },
                finish: {
                    title: 'Zakończ rysowanie',
                    text: 'Zakończ'
                },
                undo: {
                    title: 'Usuń ostatnio narysowany punkt',
                    text: 'Usuń ostatni punkt'
                },
                buttons: {
                    polyline: 'Rysuj wielolinię',
                    polygon: 'Rysuj wielokąt',
                    rectangle: 'Rysuj prostokąt',
                    circle: 'Rysuj koło',
                    marker: 'Rysuj znacznik',
                    circlemarker: 'Rysuj znacznik - koło'
                }
            },
            handlers: {
                circle: {
                    tooltip: {
                        start: 'Kliknij i przeciągnij, aby rysować koło'
                    },
                    radius: 'Radius'
                },
                circlemarker: {
                    tooltip: {
                        start: 'Kliknij, aby postawić znacznik - koło'
                    }
                },
                marker: {
                    tooltip: {
                        start: 'Kliknij, aby postawić znacznik'
                    }
                },
                polygon: {
                    tooltip: {
                        start: 'kliknij, aby narysować pierwszy punkt wielokąta',
                        cont: 'Kliknij, aby narysować kolejny punkt',
                        end: 'Kliknij na pierwszy punkt, aby zakończyć rysowanie'
                    }
                },
                polyline: {
                    error: '<strong>Error:</strong> shape edges cannot cross!',
                    tooltip: {
                        start: 'Kliknij, aby zacząć rysować linię',
                        cont: 'Kliknij, aby kontynuować rysowanie linii',
                        end: 'Kliknij na ostatni punkt, aby zakończyć rysowanie'
                    }
                },
                rectangle: {
                    tooltip: {
                        start: 'Kliknij i przeciągnij, aby narysować prostokąt'
                    }
                },
                simpleshape: {
                    tooltip: {
                        end: 'Puść przycisk, aby przestać rysować'
                    }
                }
            }
        },
        edit: {
            toolbar: {
                actions: {
                    save: {
                        title: 'Zapisz zmiany',
                        text: 'Zapisz'
                    },
                    cancel: {
                        title: 'Anuluj edytowanie i nie zapisuj zmian',
                        text: 'Anuluj'
                    },
                    clearAll: {
                        title: 'Wyczyść wszystkie warstwy',
                        text: 'Wyczyść wszystkie'
                    }
                },
                buttons: {
                    edit: 'Edytuj warstwy',
                    editDisabled: 'Brak warstw do edytowania',
                    remove: 'Usuń warstwy',
                    removeDisabled: 'Brak warstw do usunięcia'
                }
            },
            handlers: {
                edit: {
                    tooltip: {
                        text: 'Przeciągaj uchwyty, aby edytować',
                        subtext: 'Wciśnij anuluj, aby cofnąć zmiany'
                    }
                },
                remove: {
                    tooltip: {
                        text: 'Kliknij na figurę, aby ją usunąć'
                    }
                }
            }
        }
    };
}

function createDrawControl(shapeLayer, figureColor) {
    createDrawLocal();
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