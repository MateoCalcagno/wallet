import { useState } from 'react'

export function useAmountInput() {
  const [display, setDisplay] = useState('')
  const [parsed, setParsed] = useState(null)

  const handleChange = (e) => {
    let val = e.target.value.replace(/[^0-9,]/g, '')

    const partes = val.split(',')
    if (partes.length > 2) return
    if (partes[1]?.length > 2) return

    const entero = partes[0].replace(/\./g, '')
    const enteroFormateado = entero
      ? Number(entero).toLocaleString('es-AR').replace(/,.*/, '')
      : ''

    const nuevo = partes.length === 2
      ? `${enteroFormateado},${partes[1]}`
      : enteroFormateado

    setDisplay(nuevo)

    const num = parseFloat(nuevo.replace(/\./g, '').replace(',', '.'))
    setParsed(isNaN(num) ? null : num)
  }

  return { display, parsed, handleChange }
}