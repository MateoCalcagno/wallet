import MoneyAction from './MoneyAction'

const config = {
  panelTitle: "Depositá dinero,\nal instante.",
  panelSubtitle: "Elegí tu método de pago y agregá saldo en segundos.",
  title: "Depositar",
  subtitle: "Elegí el método y el monto a acreditar",
  endpoint: "/wallet/deposit",
  buttonLabel: "Depositar",
  successMessage: "¡Depósito exitoso! Redirigiendo...",
  paymentMethods: [
    { value: 'BANK_TRANSFER', label: 'Transferencia bancaria', commission: 'Sin comisión' },
    { value: 'DEBIT_CARD',    label: 'Tarjeta de débito',      commission: '1% de comisión' },
    { value: 'CREDIT_CARD',   label: 'Tarjeta de crédito',     commission: '3% de comisión' },
  ],
}

export default function Deposit() {
  return <MoneyAction config={config} />
}