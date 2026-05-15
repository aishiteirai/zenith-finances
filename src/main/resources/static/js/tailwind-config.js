tailwind.config = {
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "primary": "#eaffde",
                "primary-container": "#00ff00",
                "on-primary-container": "#027100",
                "background": "#121414",
                "surface": "#121414",
                "on-surface": "#e3e2e2",
                "on-surface-variant": "#b9ccaf",
                "outline-variant": "#3b4b35",
                "surface-container-low": "#1b1c1c",
                "surface-container-lowest": "#0d0e0f",
                "primary-fixed": "#77ff61",
                "primary-fixed-dim": "#02e600",
                "on-primary": "#013a00",
                "surface-container-high": "#292a2a",
                "error": "#ffb4ab",
                "error-container": "#93000a",
                "secondary": "#c9c6c5",
                "on-secondary": "#313030"
                // Adicione as demais cores do seu tema aqui...
            },
            fontFamily: {
                "headline-lg": ["Inter"],
                "headline-lg-mobile": ["Inter"],
                "display-lg": ["Inter"],
                "data-lg": ["JetBrains Mono"],
                "label-caps": ["Inter"],
                "body-md": ["Inter"],
                "data-sm": ["JetBrains Mono"]
            }
        }
    }
}