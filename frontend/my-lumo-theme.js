import '@polymer/polymer/lib/elements/custom-style.js';
import '@vaadin/vaadin-lumo-styles/badge.js';
const documentContainer = document.createElement('template');

documentContainer.innerHTML = `
<custom-style>
  <style include="lumo-badge"></style>
  <style>

html {
  --lumo-font-family: Roboto, Frutiger, "Frutiger Linotype", Univers, Calibri, "Gill Sans", "Gill Sans MT", "Myriad Pro", Myriad, "DejaVu Sans Condensed", "Liberation Sans", "Nimbus Sans L", Tahoma, Geneva, "Helvetica Neue", Helvetica, Arial, sans-serif;
  --lumo-font-size: 1rem;
  --lumo-font-size-xxxl: 3rem;
  --lumo-font-size-xxl: 2.25rem;
  --lumo-font-size-xl: 1.75rem;
  --lumo-font-size-l: 1.375rem;
  --lumo-font-size-m: 1.125rem;
  --lumo-font-size-s: 1rem;
  --lumo-font-size-xs: 0.875rem;
  --lumo-font-size-xxs: 0.8125rem;
  --lumo-line-height-m: 1.8;
  --lumo-line-height-s: 1.5;
  --lumo-line-height-xs: 1.3;
  --lumo-border-radius: 0.5em;
  --lumo-size-xl: 4.5rem;
  --lumo-size-l: 4rem;
  --lumo-size-m: 3rem;
  --lumo-size-s: 2.5rem;
  --lumo-size-xs: 2rem;
  --lumo-space-xl: 4rem;
  --lumo-space-l: 3rem;
  --lumo-space-m: 2rem;
  --lumo-space-s: 1rem;
  --lumo-space-xs: 0.5rem;
  --lumo-primary-text-color: rgb(78, 78, 78);
  --lumo-primary-color-50pct: rgba(78, 78, 78, 0.5);
  --lumo-primary-color-10pct: rgba(78, 78, 78, 0.1);
  --lumo-primary-color: #4E4E4E;
  --lumo-shade-5pct: rgba(0, 0, 0, 0.05);
  --lumo-shade-10pct: rgba(0, 0, 0, 0.1);
  --lumo-shade-20pct: rgba(0, 0, 0, 0.2);
  --lumo-shade-30pct: rgba(0, 0, 0, 0.3);
  --lumo-shade-40pct: rgba(0, 0, 0, 0.4);
  --lumo-shade-50pct: rgba(0, 0, 0, 0.5);
  --lumo-shade-60pct: rgba(0, 0, 0, 0.6);
  --lumo-shade-70pct: rgba(0, 0, 0, 0.7);
  --lumo-shade-80pct: rgba(0, 0, 0, 0.8);
  --lumo-shade-90pct: rgba(0, 0, 0, 0.9);
  --lumo-shade: hsl(41, 0%, 0%);
  --lumo-tint-5pct: rgba(250, 241, 227, 0.05);
  --lumo-tint-10pct: rgba(250, 241, 227, 0.1);
  --lumo-tint-20pct: rgba(250, 241, 227, 0.2);
  --lumo-tint-30pct: rgba(250, 241, 227, 0.3);
  --lumo-tint-40pct: rgba(250, 241, 227, 0.4);
  --lumo-tint-50pct: rgba(250, 241, 227, 0.5);
  --lumo-tint-60pct: rgba(250, 241, 227, 0.6);
  --lumo-tint-70pct: rgba(250, 241, 227, 0.7);
  --lumo-tint-80pct: rgba(250, 241, 227, 0.8);
  --lumo-tint-90pct: rgba(250, 241, 227, 0.9);
  --lumo-tint: #FAF1E3;
  --lumo-base-color: #FFFFFF;
  --lumo-header-text-color: #555555;
}

[theme~="dark"] {
}

  </style>
</custom-style>


<dom-module id="text-field-style" theme-for="vaadin-text-field">
  <template>
    <style>[part="input-field"]{box-shadow:inset 0 0 0 1px var(--lumo-contrast-30pct);background-color:var(--lumo-base-color);}:host([invalid]) [part="input-field"]{box-shadow:inset 0 0 0 1px var(--lumo-error-color);}
    </style>
  </template>
</dom-module>`;

document.head.appendChild(documentContainer.content);