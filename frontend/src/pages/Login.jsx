import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import FormPage from '../components/FormPage'
import api from '../api/axios'
import IconInput from '../components/IconInput'

function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [showPassword, setShowPassword] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setIsLoading(true)
    document.body.style.cursor = 'wait'  
    if (!email.trim() || !password.trim()) {
      setError('Ingresá tu email y contraseña')
      setIsLoading(false)
      document.body.style.cursor = 'default'
      return
    }
    try {
      const res = await api.post('/auth/login', { email, password })
      localStorage.setItem('token', res.data.token)
      navigate('/dashboard')
    } catch (err) {
      const msg = err.response?.data?.message || ''
      if (msg.toLowerCase().includes('invalid credentials')) {
        setError('Email o contraseña incorrectos, revisá tus datos')
      } else {
        setError('Ocurrió un error al iniciar sesión')
      }
    } finally {
      setIsLoading(false)
      document.body.style.cursor = 'default'  
    }
  }

  return (
    <FormPage panelTitle={"Tu dinero,\nbajo control."} panelSubtitle="Administrá tu saldo, transferí y hacé depósitos en segundos.">
          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">Bienvenido de nuevo</h1>
            <p className="text-sm text-gray-500">Ingresá a tu cuenta para continuar</p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Email</label>
              <IconInput
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                placeholder="tu@email.com"
                iconPath="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Contraseña</label>
              <div className="relative">
                <IconInput
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  className="pr-10"
                  iconPath="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
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
              <div className="text-right mt-1.5">
                <span
                  onClick={() => navigate('/forgot-password')}
                  className="text-xs text-blue-600 cursor-pointer hover:underline"
                >
                  ¿Olvidaste tu contraseña?
                </span>
              </div>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className={`w-full bg-blue-600 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 ${
                isLoading ? 'cursor-wait opacity-75' : 'hover:bg-blue-700 cursor-pointer'
              }`}
            >
              {isLoading ? 'Ingresando...' : 'Ingresar'}
            </button>
          </form>

          <div className="flex items-center gap-3 my-5">
            <div className="flex-1 h-px bg-gray-100" />
            <span className="text-xs text-gray-400">o</span>
            <div className="flex-1 h-px bg-gray-100" />
          </div>

          <p className="text-center text-sm text-gray-500">
            ¿No tenés cuenta?{' '}
            <span
              className="text-blue-600 font-medium cursor-pointer hover:underline"
              onClick={() => navigate('/register')}
            >
              Registrate gratis
            </span>
          </p>
    </FormPage>
  )
}

export default Login