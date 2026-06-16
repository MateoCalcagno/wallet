export const getIconConfig = (t) => {
  if (t.type === 'DEPOSIT')
    return { bg: 'bg-blue-50', color: 'text-blue-500', icon: 'M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4' }
  if (t.type === 'WITHDRAWAL')
    return { bg: 'bg-orange-50', color: 'text-orange-500', icon: 'M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12' }
  if (t.direction === 'SENT')
    return { bg: 'bg-red-50', color: 'text-red-500', icon: 'M7 11l5-5m0 0l5 5m-5-5v12' }
  return { bg: 'bg-green-50', color: 'text-green-500', icon: 'M17 13l-5 5m0 0l-5-5m5 5V6' }
}

export const getLabel = (t) => {
  if (t.type === 'DEPOSIT') return 'Depósito'
  if (t.type === 'WITHDRAWAL') return 'Retiro'
  if (t.direction === 'SENT') return `Enviado a ${t.counterpartName}`
  return `Recibido de ${t.counterpartName}`
}

export const getAmountColor = (t) => {
  if (t.type === 'DEPOSIT' || t.direction === 'RECEIVED') return 'text-green-500'
  return 'text-red-500'
}

export const getAmountPrefix = (t) => {
  if (t.type === 'DEPOSIT' || t.direction === 'RECEIVED') return '+'
  return '-'
}

export const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return date.toLocaleString('es-AR', {
    day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
  })
}