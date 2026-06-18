import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import FormPage from '../components/FormPage'
import IconInput from '../components/IconInput'

function ForgotPassword() {
  const [step, setStep] = useState(1)
  const [email, setEmail] = useState('')
  const [pin, setPin] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleSendPin = async (e) => {
    e.preventDefault()
    setError('')
    try {
      setLoading(true)
      await api.post('/users/send-verification', { email })
      setStep(2)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al enviar el código')
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyPin = async (e) => {
    e.preventDefault()
    setError('')
    try {
      setLoading(true)
      await api.post('/users/verify-pin', { email, pin })
      setStep(3)
    } catch (err) {
      setError(err.response?.data?.message || 'Código incorrecto o expirado')
    } finally {
      setLoading(false)
    }
  }

  const handleReset = async (e) => {
    e.preventDefault()
    setError('')
    try {
      setLoading(true)
      await api.post('/users/forgot-password/reset', { email, newPassword })
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || 'Error al cambiar la contraseña')
    } finally {
      setLoading(false)
    }
  }

  return (
    <FormPage panelTitle={"Recuperá\ntu acceso."} panelSubtitle="Te enviamos un código para que puedas crear una nueva contraseña.">
          {step !== 1 && (
            <button
              onClick={() => { setStep(step - 1); setError('') }}
              className="text-sm text-gray-400 hover:text-gray-600 mb-6 flex items-center gap-1"
            >
              ← Volver
            </button>
          )}

          {/* Step 1 — Email */}
          {step === 1 && (
            <>
              <div className="mb-8">
                <h1 className="text-xl font-medium text-gray-900 mb-1">Olvidaste tu contraseña</h1>
                <p className="text-sm text-gray-500">Ingresá tu email y te mandamos un código</p>
              </div>
              {error && <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">{error}</div>}
              <form onSubmit={handleSendPin} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-gray-500 mb-1.5">Email</label>
                  <IconInput
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="tu@email.com"
                    iconPath="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !email}
                  className="w-full bg-blue-600 hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60 text-white py-2.5 rounded-lg text-sm font-medium transition cursor-pointer"
                >
                  {loading ? 'Enviando...' : 'Enviar código'}
                </button>
              </form>
              <p className="text-center text-sm text-gray-500 mt-6">
                <span onClick={() => navigate('/login')} className="text-blue-600 font-medium cursor-pointer hover:underline">
                  Volver al login
                </span>
              </p>
            </>
          )}

          {/* Step 2 — PIN */}
          {step === 2 && (
            <>
              <div className="mb-8">
                <h1 className="text-xl font-medium text-gray-900 mb-1">Verificá tu email</h1>
                <p className="text-sm text-gray-500">Ingresá el código que enviamos a <span className="font-medium text-gray-700">{email}</span></p>
              </div>
              {error && <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">{error}</div>}
              <form onSubmit={handleVerifyPin} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-gray-500 mb-1.5">Código de verificación</label>
                  <input
                    type="text"
                    value={pin}
                    onChange={(e) => setPin(e.target.value.replace(/\D/g, '').slice(0, 6))}
                    placeholder="123456"
                    maxLength={6}
                    className="w-full px-4 py-2.5 text-sm text-center tracking-widest border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || pin.length !== 6}
                  className="w-full bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white py-2.5 rounded-lg text-sm font-medium transition cursor-pointer"
                >
                  {loading ? 'Verificando...' : 'Verificar código'}
                </button>
              </form>
              <p className="text-center text-sm text-gray-500 mt-6">
                ¿No recibiste el código?{' '}
                <span onClick={handleSendPin} className="text-blue-600 font-medium cursor-pointer hover:underline">Reenviar</span>
              </p>
            </>
          )}

          {/* Step 3 — Nueva contraseña */}
          {step === 3 && (
            <>
              <div className="mb-8">
                <h1 className="text-xl font-medium text-gray-900 mb-1">Nueva contraseña</h1>
                <p className="text-sm text-gray-500">Elegí una contraseña segura para tu cuenta</p>
              </div>
              {error && <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">{error}</div>}
              <form onSubmit={handleReset} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-gray-500 mb-1.5">Nueva contraseña</label>
                  <div className="relative">
                    <IconInput
                      type={showPassword ? 'text' : 'password'}
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="Mínimo 8 caracteres"
                      className="pr-10"
                      iconPath="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                    >
                      {showPassword ? (
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />
                        </svg>
                      ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                          <path strokeLinecap="round" strokeLinejoin="round" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                        </svg>
                      )}
                    </button>
                  </div>
                  {newPassword.length > 0 && (
                    <p className={`text-xs mt-1.5 ${newPassword.length >= 8 ? 'text-green-500' : 'text-gray-400'}`}>
                      {newPassword.length >= 8 ? '✓ Contraseña válida' : `${8 - newPassword.length} caracteres más`}
                    </p>
                  )}
                </div>
                <button
                  type="submit"
                  disabled={loading || newPassword.length < 8}
                  className="w-full bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white py-2.5 rounded-lg text-sm font-medium transition cursor-pointer"
                >
                  {loading ? 'Guardando...' : 'Cambiar contraseña'}
                </button>
              </form>
            </>
          )}
    </FormPage>
  )
}

export default ForgotPassword