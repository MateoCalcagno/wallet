import MoneyAction from './MoneyAction'

const config = {
  panelTitle: "Retirá tu dinero\ncuando quieras.",
  panelSubtitle: "Mové fondos a tu cuenta externa fácilmente.",
  title: "Retirar dinero",
  subtitle: "Ingresá el monto a retirar",
  endpoint: "/wallet/withdraw",
  buttonLabel: "Retirar",
  successMessage: "¡Retiro exitoso! Redirigiendo...",
  errorMessages: {
    insufficient: "Saldo insuficiente para realizar el retiro",
  },
}

export default function Withdraw() {
  return <MoneyAction config={config} />
}