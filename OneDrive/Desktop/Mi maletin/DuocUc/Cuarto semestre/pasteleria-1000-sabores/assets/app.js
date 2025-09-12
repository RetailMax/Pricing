// Este es el "cerebro" de la pastelería. Se encarga de todo lo que pasa "detrás de cámaras".

// === 1. Datos de la pastelería ===
// Esta es la lista de todos los productos que vendemos.
const productos = [
  {codigo:'TC001', categoria:'Tortas Cuadradas', nombre:'Torta Cuadrada de Chocolate', precio:45000, forma:'cuadrada', tamano:'mediana', imagen:'assets/Imagenes/Torta Selva negra.jpg'},
  {codigo:'TC002', categoria:'Tortas Cuadradas', nombre:'Torta Cuadrada de Frutas', precio:50000, forma:'cuadrada', tamano:'mediana', imagen:'assets/Imagenes/Torta frutas.jpg'},
  {codigo:'TT001', categoria:'Tortas Circulares', nombre:'Torta Circular de Vainilla', precio:40000, forma:'circular', tamano:'mediana', imagen:'assets/Imagenes/Torta Vainilla.jpg'},
  {codigo:'TT002', categoria:'Tortas Circulares', nombre:'Torta Circular de Manjar', precio:42000, forma:'circular', tamano:'mediana', imagen:'assets/Imagenes/MAnjar torta.jpg'},
  {codigo:'PI001', categoria:'Postres Individuales', nombre:'Mousse de Chocolate', precio:5000, forma:'', tamano:'pequena', imagen:'assets/Imagenes/mousse.jpg'},
  {codigo:'PI002', categoria:'Postres Individuales', nombre:'Tiramisú Clásico', precio:5500, forma:'', tamano:'pequena', imagen:'assets/Imagenes/tiramisu.jpg'},
  {codigo:'PSA001', categoria:'Productos Sin Azúcar', nombre:'Torta Sin Azúcar de Naranja', precio:48000, forma:'circular', tamano:'mediana', imagen:'assets/Imagenes/naranja.jpg'},
  {codigo:'PSA002', categoria:'Productos Sin Azúcar', nombre:'Cheesecake Sin Azúcar', precio:47000, forma:'', tamano:'mediana', imagen:'assets/Imagenes/cheesecake.jpg'},
  {codigo:'PT001', categoria:'Pastelería Tradicional', nombre:'Empanada de Manzana', precio:3000, forma:'', tamano:'pequena', imagen:'assets/Imagenes/manzana.jpg'},
  {codigo:'PT002', categoria:'Pastelería Tradicional', nombre:'Tarta de Santiago', precio:6000, forma:'', tamano:'pequena', imagen:'assets/Imagenes/Tarta española.jpg'},
  {codigo:'PG001', categoria:'Productos Sin Gluten', nombre:'Brownie Sin Gluten', precio:4000, forma:'', tamano:'pequena', imagen:'assets/Imagenes/brownie.jpg'},
  {codigo:'PG002', categoria:'Productos Sin Gluten', nombre:'Pan Sin Gluten', precio:3500, forma:'', tamano:'pequena', imagen:'assets/Imagenes/pan.jpg'},
  {codigo:'PV001', categoria:'Productos Vegana', nombre:'Torta Vegana de Chocolate', precio:50000, forma:'circular', tamano:'mediana', imagen:'assets/Imagenes/Torta Selva negra.jpg'},
  {codigo:'PV002', categoria:'Productos Vegana', nombre:'Galletas Veganas de Avena', precio:4500, forma:'', tamano:'pequena', imagen:'assets/Imagenes/galleta.avif'},
  {codigo:'TE001', categoria:'Tortas Especiales', nombre:'Torta Especial de Cumpleaños', precio:55000, forma:'circular', tamano:'grande', imagen:'assets/Imagenes/cumple.jpg'},
  {codigo:'TE002', categoria:'Tortas Especiales', nombre:'Torta Especial de Boda', precio:60000, forma:'circular', tamano:'grande', imagen:'assets/Imagenes/boda.jpg'},
];



// Esta función nos ayuda a guardar y obtener datos en el navegador.
// Es como un cajón donde guardamos el carrito y el perfil del cliente.
const storage = {
  get(key, def){ try{ return JSON.parse(localStorage.getItem(key)) ?? def }catch{ return def } },
  set(key, val){ localStorage.setItem(key, JSON.stringify(val)) }
};



// Esta función se encarga de mostrar los productos en la página de catálogo.
(function initCatalogo(){
  const cont = document.querySelector("#listado");
  const form = document.querySelector("#filtros");
  // Si no encuentra el contenedor o el formulario, no hace nada.
  if(!cont || !form) return;

  const render = () => {
    // Obtenemos lo que el usuario escribió en el formulario (búsqueda y filtros).
    const fd = new FormData(form);
    const q = (fd.get('q') || '').toString().toLowerCase();
    const categoria = fd.get('categoria') || '';
    const forma = fd.get('forma') || '';
    const tamano = fd.get('tamano') || '';

    // Creamos una nueva lista de productos que cumplen con los filtros.
    const items = [];
    for(const p of productos){
      const okCat = !categoria || p.categoria === categoria;
      const okForma = !forma || p.forma === forma;
      const okTam = !tamano || p.tamano === tamano;
      const okQ = !q || p.nombre.toLowerCase().includes(q) || p.codigo.toLowerCase().includes(q);
      
      if(okCat && okForma && okTam && okQ){
        items.push(p);
      }
    }

    // Borramos lo que había antes y preparamos el nuevo contenido.
    cont.innerHTML = ''; // Limpiamos
    let html = ''; // Nuevo contenido
for(const p of items){  // Recorremos los productos que cumplen los filtros
      html += ` <!-- Cada producto va en una columna responsiva -->
        <div class="col-12 col-sm-6 col-md-4 col-lg-3"> <!-- Ajuste de clases para responsividad -->
          <article class="product" data-codigo="${p.codigo}"> <!-- Cada producto es un artículo -->
            <img src="${p.imagen}" class="img-fluid" alt="${p.nombre}"> <!-- Imagen del producto -->
            <div class="info"> <!-- Información del producto -->
              <h4>${p.nombre}</h4> <!-- Nombre del producto -->
              <small>${p.categoria} · Código ${p.codigo}</small> <!-- Categoría y código -->
              <div class="price">$${p.precio.toLocaleString('es-CL')}</div> <!-- Precio formateado -->
            </div> <!-- Fin de la información -->
            <div class="actions"> <!-- Botones de acción -->
              <button class="btn add" data-codigo="${p.codigo }">Agregar</button> <!-- Botón para agregar al carrito -->
              <a class="btn btn-outline" href="carrito.html">Ir al carrito</a> <!-- Botón para ir al carrito -->
            </div>
          </article>
        </div>
      `;
    }
    cont.innerHTML = html;

    // Activamos los botones de "Agregar"
    cont.querySelectorAll('button.add').forEach(btn => {
      btn.addEventListener('click', e => {
        const codigo = e.currentTarget.dataset.codigo;
        addToCart(codigo);
      });
    });
  };

  // Re-render al cambiar filtros
  form.addEventListener('input', render);
  render();
})();



// Esta función agrega un producto al carrito.
function addToCart(codigo){
  // Obtenemos el carrito del "cajón" del navegador (o un carrito vacío si no hay uno).
  const cart = storage.get('cart', []);
  
  // Buscamos si el producto ya está en el carrito.
  let itemFound = false;
  for(const item of cart){
    if(item.codigo === codigo){
      item.cantidad += 1; // Si lo encontramos, solo aumentamos la cantidad.
      itemFound = true;
      break; // Salimos del ciclo porque ya encontramos el producto.
    }
  }

  // Si el producto no estaba en el carrito, lo agregamos.
  if(!itemFound){
    // Buscamos el producto en nuestra lista de productos de la pastelería.
    let prod = null;
    for(const p of productos){
      if(p.codigo === codigo){
        prod = p;
        break;
      }
    }
    if(!prod) return; // Si no lo encontramos, no hacemos nada.
    // Agregamos el producto al carrito con una cantidad de 1.
    cart.push({codigo, precio: prod.precio, nombre: prod.nombre, cantidad: 1});
  }
  
  // Guardamos el carrito actualizado en el "cajón" del navegador.
  storage.set('cart', cart);
  alert('Producto agregado al carrito');
}

// Esta función muestra el contenido del carrito en la página.
(function initCarrito(){
  const mount = document.querySelector('#cart');
  if(!mount) return;

  const cart = storage.get('cart', []);
  const perfil = storage.get('perfil', null);

  // Calculamos el subtotal del carrito.
  let subtotal = 0;
  for(const item of cart){
    subtotal += item.precio * item.cantidad;
  }

  // Calculamos los descuentos.
  const descuentos = calcularDescuentos(perfil, subtotal);

  // Creamos el HTML para mostrar cada producto en el carrito.
  let htmlCarrito = '';
  if(cart.length === 0){
    htmlCarrito = '<p>Tu carrito está vacío. <a href="catalogo.html">Ir al catálogo</a></p>';
  } else {
    for(const item of cart){
      htmlCarrito += `
        <div class="cart-line">
          <div><strong>${item.nombre}</strong><br><small>Código ${item.codigo}</small></div>
          <div>Cant: ${item.cantidad}</div>
          <div>Unit: $${item.precio.toLocaleString('es-CL')}</div>
          <div>Total: $${(item.precio*item.cantidad).toLocaleString('es-CL')}</div>
        </div>
      `;
    }
  }

  // Ahora, armamos el HTML completo, incluyendo los totales y descuentos.
  mount.innerHTML = `
    ${htmlCarrito}
    <div class="cart-total">
      <div>Subtotal: $${subtotal.toLocaleString('es-CL')}</div>
      <div>Descuentos: -$${descuentos.monto.toLocaleString('es-CL')} <small>(${descuentos.detalle.join(' + ') || 'N/A'})</small></div>
      <div>Total: $${(subtotal - descuentos.monto).toLocaleString('es-CL')}</div>
    </div>
  `;
})();

// Esta función revisa si el usuario tiene descuentos aplicables.
function calcularDescuentos(perfil, subtotal){
  let monto = 0;
  const detalle = [];
  // Si no hay un perfil de usuario, no hay descuentos.
  if(!perfil) return {monto, detalle};

  // Descuento para mayores de 50
  if(perfil.esMayor50){
    const d = subtotal * 0.50;
    monto += d;
    detalle.push('50% adulto mayor');
  }
  
  // Descuento por código FELICES50
  if(perfil.codigoValido){
    const d = subtotal * 0.10;
    monto += d;
    detalle.push('10% FELICES50');
  }
  
  // Torta gratis para estudiantes de DUOC en su cumpleaños
  if(perfil.esDuoc && hayTortaEnCarrito()){
    const hoy = new Date(); 
    const fn = new Date(perfil.fechaNac);
    // Verificamos si la fecha de hoy coincide con el cumpleaños (día y mes).
    if(hoy.getDate() === fn.getDate() && hoy.getMonth() === fn.getMonth()){
      const cart = storage.get('cart', []);
      let tortaEncontrada = null;
      // Buscamos la primera torta en el carrito (productos con código que empieza en 'T').
      for(const item of cart){
        if(item.codigo.startsWith('T')){
          tortaEncontrada = item;
          break;
        }
      }
      if(tortaEncontrada){
        const d = tortaEncontrada.precio;
        monto += d;
        detalle.push('Torta gratis por cumpleaños DUOC');
      }
    }
  }
  
  // Nos aseguramos de que el descuento no sea mayor que el subtotal.
  if(monto > subtotal) {
    monto = subtotal;
  }
  return {monto, detalle};
}

// Esta función revisa si hay al menos una torta en el carrito.
function hayTortaEnCarrito(){
  const cart = storage.get('cart', []);
  for(const item of cart){
    if(item.codigo.startsWith('T')){
      return true; // Si encuentra una, devuelve 'true' de inmediato.
    }
  }
  return false; // Si recorre todo el carrito y no encuentra, devuelve 'false'.
}


// Se encarga de la validación del formulario de registro.
(function initRegistro(){
  const form = document.querySelector('#form-registro');
  const resumen = document.querySelector('#resumen-beneficios');
  if(!form) return;

  // Referencias a los campos del formulario.
  const fields = {
    nombre: form.querySelector('#nombre'),
    email: form.querySelector('#email'),
    fechaNac: form.querySelector('#fechaNac'),
    codigo: form.querySelector('#codigo'),
    acepto: form.querySelector('#acepto'),
  };
  
  // Referencias a los espacios de error.
  const errors = {
    nombre: fields.nombre?.closest('.field')?.querySelector('.error'),
    email: fields.email?.closest('.field')?.querySelector('.error'),
    fechaNac: fields.fechaNac?.closest('.field')?.querySelector('.error'),
    codigo: fields.codigo?.closest('.field')?.querySelector('.error'),
    acepto: fields.acepto?.closest('.field')?.querySelector('.error'),
  };

  // Función para mostrar un mensaje de error en un campo específico.
  function setError(input, msg){
    const box = input.closest('.field').querySelector('.error');
    box.textContent = msg || '';
  }

  // Función para calcular la edad de una persona.
  function calcEdad(fechaStr){
    const f = new Date(fechaStr);
    if(isNaN(f)) return 0;
    const hoy = new Date();
    let edad = hoy.getFullYear() - f.getFullYear();
    const m = hoy.getMonth() - f.getMonth();
    if(m < 0 || (m === 0 && hoy.getDate() < f.getDate())) edad--;
    return edad;
  }

  // Funciones de validación para cada campo.
  function validateNombre(){
    const v = fields.nombre.value.trim();
    if(v.length < 3){ setError(fields.nombre, 'Por favor ingresa tu nombre y apellido.'); return false; }
    setError(fields.nombre, ''); return true;
  }
  function validateEmail(){
    const v = fields.email.value.trim();
    if(!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)){ setError(fields.email, 'Ingresa un correo válido (ej. nombre@dominio.com).'); return false; }
    setError(fields.email, '');
    const box = fields.email.closest('.field').querySelector('.hint');
    if(v.endsWith('@duoc.cl')){
      box.textContent = '¡Genial! Con correo @duoc.cl podrás obtener torta gratis en tu cumpleaños.';
    } else {
      box.textContent = 'Si usas tu correo @duoc.cl puedes acceder a beneficios especiales.';
    }
    return true;
  }
  function validateFecha(){
    const v = fields.fechaNac.value;
    if(!v){ setError(fields.fechaNac, 'Selecciona tu fecha de nacimiento.'); return false; }
    const edad = calcEdad(v);
    if(edad < 0 || edad > 120){ setError(fields.fechaNac, 'Revisa la fecha, parece incorrecta.'); return false; }
    setError(fields.fechaNac, ''); 
    const hint = fields.fechaNac.closest('.field').querySelector('.hint');
    if(edad >= 50){ hint.textContent = '¡Aplica 50% de descuento por ser mayor de 50!'; }
    else { hint.textContent = 'Mayores de 50 años obtienen 50% de descuento.'; }
    return true;
  }
  function validateCodigo(){
    const v = fields.codigo.value.trim();
    if(v && v.toUpperCase() !== 'FELICES50'){ setError(fields.codigo, 'Código inválido. ¿Quisiste decir FELICES50?'); return false; }
    setError(fields.codigo, ''); return true;
  }
  function validateAcepto(){
    if(!fields.acepto.checked){ setError(fields.acepto, 'Debes aceptar los términos para continuar.'); return false; }
    setError(fields.acepto, ''); return true;
  }

  const validators = [validateNombre, validateEmail, validateFecha, validateCodigo, validateAcepto];

  // Validamos cada vez que el usuario escribe en un campo.
  form.addEventListener('input', (e)=>{
    const id = e.target.id;
    const map = {nombre:validateNombre, email:validateEmail, fechaNac:validateFecha, codigo:validateCodigo, acepto:validateAcepto};
    if(map[id]) map[id]();
  });

  // Cuando el usuario envía el formulario...
  form.addEventListener('submit', (e)=>{
    e.preventDefault(); // Evitamos que la página se recargue.
    
    // Revisamos si todos los campos son válidos.
    const ok = validators.every(fn => fn());
    if(!ok) return; // Si algo falla, detenemos el proceso.

    // Creamos un objeto con los datos del perfil y sus beneficios.
    const perfil = {
      nombre: fields.nombre.value.trim(),
      email: fields.email.value.trim(),
      fechaNac: fields.fechaNac.value,
      esMayor50: calcEdad(fields.fechaNac.value) >= 50,
      codigoValido: (fields.codigo.value.trim().toUpperCase() === 'FELICES50'),
      esDuoc: fields.email.value.trim().toLowerCase().endsWith('@duoc.cl')
    };
    
    // Guardamos el perfil en el navegador.
    storage.set('perfil', perfil);

    // Preparamos el resumen de beneficios y lo mostramos.
    resumen.hidden = false;
    let tips = '';
    if(perfil.esMayor50) tips += '<li>Tendrás 50% de descuento en todos los productos.</li>';
    else tips += '<li>Recibirás promociones personalizadas.</li>';
    if(perfil.codigoValido) tips += '<li>Se aplicará 10% adicional por código FELICES50.</li>';
    else tips += '<li>Agrega el código FELICES50 para 10% de por vida.</li>';
    if(perfil.esDuoc) tips += '<li>Con correo @duoc.cl podrás obtener una torta gratis en tu cumpleaños.</li>';
    else tips += '<li>Usa correo @duoc.cl para beneficios en cumpleaños.</li>';
    
    resumen.innerHTML = `<h3>¡Bienvenido/a, ${perfil.nombre.split(' ')[0]}!</h3><ul class="checklist">${tips}</ul>`;
    window.scrollTo({top: resumen.offsetTop - 20, behavior:'smooth'});
  });
})();

// Se encarga de la validación del formulario de pago.
(function initPago(){
  const form = document.querySelector('#form-pago');
  const okMsg = document.querySelector('#confirmacion');
  if(!form) return;

  // Referencias a los campos del formulario de pago.
  const fields = {
    direccion: form.querySelector('#direccion'),
    fechaEntrega: form.querySelector('#fechaEntrega'),
    mensaje: form.querySelector('#mensaje'),
  };
  
  // Función para mostrar mensajes de error.
  function setError(input, msg){
    const box = input.closest('.field').querySelector('.error');
    box.textContent = msg || '';
  }

  // Funciones de validación para cada campo.
  function validateDireccion(){
    const v = fields.direccion.value.trim();
    if(v.length < 5){ setError(fields.direccion, 'Ingresa una dirección válida.'); return false; }
    setError(fields.direccion, ''); return true;
  }
  function validateFechaEntrega(){
    const v = fields.fechaEntrega.value;
    if(!v){ setError(fields.fechaEntrega, 'Selecciona una fecha.'); return false; }
    const f = new Date(v);
    const hoy = new Date(); hoy.setHours(0,0,0,0);
    if(f < hoy){ setError(fields.fechaEntrega, 'La fecha debe ser hoy o posterior.'); return false; }
    setError(fields.fechaEntrega, ''); return true;
  }
  function validateMensaje(){
    const v = fields.mensaje.value;
    if(v.length > 50){ setError(fields.mensaje, 'Máximo 50 caracteres.'); return false; }
    setError(fields.mensaje, ''); return true;
  }

  const validators = [validateDireccion, validateFechaEntrega, validateMensaje];

  // Validamos cada vez que el usuario escribe en un campo.
  form.addEventListener('input', (e)=>{
    const map = {direccion:validateDireccion, fechaEntrega:validateFechaEntrega, mensaje:validateMensaje};
    if(map[e.target.id]) map[e.target.id]();
  });

  // Cuando el usuario envía el formulario...
  form.addEventListener('submit', (e)=>{
    e.preventDefault();
    const ok = validators.every(fn => fn());
    if(!ok) return;

    // Si todo está correcto, mostramos el mensaje de confirmación.
    okMsg.hidden = false;
    storage.set('cart', []); // Limpiamos el carrito para el siguiente pedido.
    okMsg.scrollIntoView({behavior:'smooth'}); // Nos movemos al mensaje de confirmación.
  });
})();