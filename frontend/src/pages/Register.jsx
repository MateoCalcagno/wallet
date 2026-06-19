import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import FormPage from '../components/FormPage'
import api from '../api/axios'
import IconInput from '../components/IconInput'

function Register() {
  const [step, setStep] = useState(1) // 1 = form, 2 = verificar PIN
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    dni: '',
    email: '',
    password: ''
  })
  const [pin, setPin] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const navigate = useNavigate()

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  // Paso 1: validar form y enviar PIN al mail
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    if (!form.firstName || !form.lastName || !form.dni || !form.email || !form.password) {
      setError('Completá todos los campos')
      return
    }
    if (!/^\d{7,8}$/.test(form.dni)) {
      setError('El DNI debe tener 7 u 8 dígitos')
      return
    }
    if (form.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres')
      return
    }

    try {
      setLoading(true)
      await api.post('/users/send-verification', { email: form.email })
      setStep(2)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al enviar el código')
    } finally {
      setLoading(false)
    }
  }

  // Paso 2: verificar PIN y crear usuario
  const handleVerify = async (e) => {
    e.preventDefault()
    setError('')

    try {
      setLoading(true)
      await api.post('/users/verify-pin', { email: form.email, pin })
      await api.post('/users', form)
      navigate('/login')
    } catch (err) {
      setError(err.response?.data?.message || 'Código incorrecto o expirado')
    } finally {
      setLoading(false)
    }
  }

  const handleResend = async () => {
    setError('')
    try {
      await api.post('/users/send-verification', { email: form.email })
    } catch (err) {
      setError('Error al reenviar el código')
    }
  }

  const fields = [
    { name: 'firstName', label: 'Nombre', type: 'text', placeholder: 'Juan',
      icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' },
    { name: 'lastName', label: 'Apellido', type: 'text', placeholder: 'Pérez',
      icon: 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z' },
    { name: 'dni', label: 'DNI', type: 'text', placeholder: '12345678',
      maxLength: 8,
      icon: 'M10 6H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V8a2 2 0 00-2-2h-5m-4 0V5a2 2 0 114 0v1m-4 0a2 2 0 104 0m-5 8a2 2 0 100-4 2 2 0 000 4zm0 0c1.306 0 2.417.835 2.83 2M9 14a3.001 3.001 0 00-2.83 2' },
    { name: 'email', label: 'Email', type: 'email', placeholder: 'tu@email.com',
      icon: 'M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z' },
  ]

  return (
    <FormPage panelTitle={"Creá tu cuenta,\nen segundos."} panelSubtitle="Empezá a usar tu billetera digital de forma simple y segura.">
          {step === 1 ? (
            <>
              <div className="mb-8">
                <h1 className="text-xl font-medium text-gray-900 mb-1">Crear cuenta</h1>
                <p className="text-sm text-gray-500">Completá tus datos para registrarte</p>
              </div>

              {error && (
                <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-4">
                {fields.map(({ name, label, type, placeholder, icon, maxLength }) => (
                  <div key={name}>
                    <label className="block text-xs font-medium text-gray-500 mb-1.5">{label}</label>
                    <IconInput
                      type={type}
                      name={name}
                      value={form[name]}
                      onChange={handleChange}
                      placeholder={placeholder}
                      maxLength={maxLength}
                      iconPath={icon}
                    />
                  </div>
                ))}

                <div>
                  <label className="block text-xs font-medium text-gray-500 mb-1.5">Contraseña</label>
                  <div className="relative">
                    <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400">
                      <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={1.5}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                      </svg>
                    </span>
                    <input
                      type={showPassword ? 'text' : 'password'}
                      name="password"
                      value={form.password}
                      onChange={handleChange}
                      placeholder="Mínimo 8 caracteres"
                      maxLength={20}
                      className="w-full pl-9 pr-10 py-2.5 text-sm border border-gray-200 rounded-lg bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 cursor-pointer"
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
                  {form.password.length > 0 && (
                    <p className={`text-xs mt-1.5 ${form.password.length >= 8 ? 'text-green-500' : 'text-gray-400'}`}>
                      {form.password.length >= 8 ? '✓ Contraseña válida' : `${8 - form.password.length} caracteres más`}
                    </p>
                  )}
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full bg-blue-600 hover:bg-blue-700 disabled:opacity-60 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 cursor-pointer"
                >
                  {loading ? 'Enviando código...' : 'Continuar'}
                </button>
              </form>

              <p className="text-center text-sm text-gray-500 mt-6">
                ¿Ya tenés cuenta?{' '}
                <span onClick={() => navigate('/login')} className="text-blue-600 font-medium cursor-pointer hover:underline">
                  Iniciá sesión
                </span>
              </p>
            </>
          ) : (
            <>
              <div className="mb-8">
                <button
                  onClick={() => { setStep(1); setError(''); setPin('') }}
                  className="text-sm text-gray-400 hover:text-gray-600 mb-4 flex items-center gap-1"
                >
                  ← Volver
                </button>
                <h1 className="text-xl font-medium text-gray-900 mb-1">Verificá tu email</h1>
                <p className="text-sm text-gray-500">
                  Ingresá el código de 6 dígitos que enviamos a <span className="font-medium text-gray-700">{form.email}</span>
                </p>
              </div>

              {error && (
                <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
                  {error}
                </div>
              )}

              <form onSubmit={handleVerify} className="space-y-4">
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
                  {loading ? 'Verificando...' : 'Verificar y crear cuenta'}
                </button>
              </form>

              <p className="text-center text-sm text-gray-500 mt-6">
                ¿No recibiste el código?{' '}
                <span onClick={handleResend} className="text-blue-600 font-medium cursor-pointer hover:underline">
                  Reenviar
                </span>
              </p>
            </>
          )}
    </FormPage>
  )
}

export default Register