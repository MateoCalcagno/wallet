import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import FormPage from '../components/FormPage'
import IconInput from '../components/IconInput'

const ICON_MONEY = "M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"

function MoneyAction({ config }) {
  const { panelTitle, panelSubtitle, title, subtitle, endpoint, buttonLabel, successMessage, errorMessages = {}, paymentMethods } = config
  const [amount, setAmount] = useState('')
  const [paymentMethod, setPaymentMethod] = useState(paymentMethods?.[0]?.value ?? null)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    const parsed = parseFloat(amount)
    if (isNaN(parsed) || parsed <= 0) {
      setError('Ingresá un monto válido')
      return
    }
    try {
      const body = { amount: parsed }
      if (paymentMethod) body.paymentMethod = paymentMethod
      await api.post(endpoint, body)
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch (err) {
      const msg = err.response?.data?.message || ''
      const matched = Object.entries(errorMessages).find(([key]) => msg.toLowerCase().includes(key))
      setError(matched ? matched[1] : 'Ocurrió un error')
    }
  }

  return (
    <FormPage panelTitle={panelTitle} panelSubtitle={panelSubtitle}>
          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">{title}</h1>
            <p className="text-sm text-gray-500">{subtitle}</p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-600">
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">

            {/* Selector de método de pago — solo si se pasa paymentMethods */}
            {paymentMethods && (
              <div>
                <label className="block text-xs font-medium text-gray-500 mb-2">Método de pago</label>
                <div className="flex flex-col gap-2">
                  {paymentMethods.map((m) => (
                    <label
                      key={m.value}
                      className={`flex items-center justify-between px-4 py-3 rounded-lg border cursor-pointer transition
                        ${paymentMethod === m.value
                          ? 'border-blue-500 bg-blue-50'
                          : 'border-gray-200 bg-gray-50 hover:border-gray-300'
                        }`}
                    >
                      <div className="flex items-center gap-3">
                        <input
                          type="radio"
                          name="paymentMethod"
                          value={m.value}
                          checked={paymentMethod === m.value}
                          onChange={() => setPaymentMethod(m.value)}
                          className="accent-blue-500"
                        />
                        <span className="text-sm text-gray-800">{m.label}</span>
                      </div>
                      <span className={`text-xs font-medium ${m.value === paymentMethods[0].value ? 'text-green-500' : 'text-orange-400'}`}>
                        {m.commission}
                      </span>
                    </label>
                  ))}
                </div>
              </div>
            )}

            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Monto</label>
              <IconInput
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="0.00"
                min="0.01"
                step="0.01"
                iconPath="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </div>

            <button type="submit" className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 cursor-pointer">
              {buttonLabel}
            </button>
            <button type="button" onClick={() => navigate('/dashboard')} className="w-full text-sm text-gray-500 hover:text-gray-700 transition py-1 cursor-pointer">
              ← Volver al inicio
            </button>
          </form>
    </FormPage>
  )
}

export default MoneyAction